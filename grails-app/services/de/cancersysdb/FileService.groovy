package de.cancersysdb

import grails.transaction.Transactional
import org.apache.commons.io.IOUtils
import org.apache.commons.logging.LogFactory
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.springframework.web.multipart.MultipartFile
import org.codehaus.groovy.grails.web.context.ServletContextHolder
import org.springframework.web.multipart.commons.CommonsMultipartFile

import java.security.DigestInputStream
import java.security.MessageDigest

/**
 * This Service Manages the File Imports and tries to minimize multiple Uploads of the same file
 */

@Transactional
class FileService {

    GrailsApplication grailsApplication

    //This is a Local store for Mock Requests
    private static final log = LogFactory.getLog(this)

    Map tempfiles = [:]

    //TODO Better File Handling, Creates a lot of Dublicates
    /**
     * Put File To disk or get Pointer to allerady Exististing file
     * @param FileToSave Multimpart File usualy from the Controller
     * @return Returns a File as Source File.
     */
    SourceFile SaveFile(MultipartFile FileToSave) {
        //Check for unsed and Deprecated files
        Clean()
        // HandleFile
        def servletContext = ServletContextHolder.servletContext
        def stream = FileToSave.getInputStream()
        MessageDigest md = MessageDigest.getInstance("MD5")
        DigestInputStream dis = new DigestInputStream(FileToSave.getInputStream(), md)
        byte[] digest = md.digest(dis.bytes)
        Long byteSize=  digest.length
        String md5 = new String(digest)
        Integer hash = md5.hashCode()

        String Filename = FileToSave.getOriginalFilename()

        Date now = new Date()
        SourceFile candidate = SourceFile.findByOriginalFilenameAndContentHash(Filename, hash)

        if (candidate) {
            def temp = new File(candidate.fileName)
            if (temp.exists()) {
                log.debug("File was Allready uploaded Returning Instance from database")
                return candidate
            }
        }

        String newFilename = now.getYear() + "_" + now.getMonth() + "_" + now.getDay() + "_" + now.getHours() + "__" + Filename
        SourceFile file
        file = new SourceFile(originalFilename: Filename, fileName: newFilename, contentHash: hash, byteSize: byteSize)



        if (!grailsApplication.config.cancersys.config.tempFilepath)
            grailsApplication.config.cancersys.config.tempFilepath = servletContext.getRealPath("UploadedFiles")

        def folder = new File(grailsApplication.config.cancersys.config.tempFilepath.toString())

        if (!folder.exists()) {
            log.debug("File Save Folder does not Exist, creating it")
            try {
                def a = new File(grailsApplication.config.cancersys.config.tempFilepath.toString())
                a.mkdirs()
            } catch (Error e) {
                log.error("cant create Folder for Uploaded Source Files, The Config Variable is not Set Properly.")

            }


        }
        File fileDest = new File(grailsApplication.config.cancersys.config.tempFilepath + newFilename)
        for (int i = 0; fileDest.exists() && i < 100; i++) {
            if (fileDest.exists()) {
                newFilename = now.getYear() + "_" + now.getMonth() + "_" + now.getDay() + "_" + now.getHours() + "_" + i + "_" + Filename
                fileDest = new File(grailsApplication.config.cancersys.config.tempFilepath + newFilename)
            }
        }

        FileToSave.transferTo(fileDest)
        file.save()
        //Exception mfor testing. The Testmockfiles cant be written to disk
        if (FileToSave.getClass().name.equals("org.codehaus.groovy.grails.plugins.testing.GrailsMockMultipartFile")) {
            log.info("This is a Testcase Putting Mockfile on Internal Stash")

            tempfiles.put(file.id, FileToSave)
        }

        return file

    }

    /**
     * Check if File Allready Exists
     * @param sf SourceFile to check
     * @return
     */
    def GetExisting(SourceFile sf) {
        //Exception mfor testing. The Testmockfiles cant be written to disk
        if (tempfiles.containsKey(sf.id)) {
            log.info("This seems tobe a Testcase Returning File ")
            String temp = IOUtils.toString(tempfiles.get(sf.id).getInputStream())
            if (temp.empty)
                log.error("There is nothing in the Testenviroment File")
            return temp
        }


        log.debug("retriving :" + grailsApplication.config.cancersys.config.tempFilepath + sf.getFileName())

        return new File(grailsApplication.config.cancersys.config.tempFilepath + sf.getFileName())


    }

    /**
     * Check if File Allready Exists
     * @param f A File from Upload
     * @return A String
     */
    String fileToString(def f) {
        String temp = null
        if (f)
            temp = IOUtils.toString(f.getInputStream())
        return temp


    }


    String Clean(){

        def MaxMegabytes = 400
        def MaxFilesToKeep =20
        def Sfs = SourceFile.getAll()
        List toKeep=[]

            Sfs.sort{it.dateCreated}
            Sfs=Sfs.reverse(true)
            Long AccumulatedBytes = 0
            Sfs.eachWithIndex{ thing,index->

                AccumulatedBytes +=thing.byteSize

                if(AccumulatedBytes > (MaxMegabytes*1024*1024) || index > MaxFilesToKeep){
                    try{
                        File f = new File( thing.fileName)
                        if(f.exists() && f.isFile())
                            f.delete()
                    }catch (Error e){

                        log.error( "Problem while cleaning Cache")
                        log.debug(e.toString())
                    }

                    thing.delete()
                }else
                    toKeep.add(thing.fileName)


            }

        File folder = new File(grailsApplication.config.cancersys.config.tempFilepath)
        if(folder.isDirectory()){
            File[] listOfFiles = folder.listFiles()
                for (int i = 0; i < listOfFiles.length; i++) {

                    File tempf = listOfFiles[i]
                    if(!toKeep.contains(tempf.absolutePath))
                        try {
                            tempf.delete()

                        }catch(Error e ){
                            log.error( "Problem while cleaning Cache")
                            log.error(  e.toString())

                        }
                }
        }
    }


}
