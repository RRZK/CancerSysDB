package asset.pipeline

/**
 * Created by rkrempel on 17.02.16.
 */
class CsvAssetFile extends AbstractAssetFile {
    static final String contentType = 'text/csv'
    static extensions = ['csv']
    static compiledExtension = 'csv'
    static processors = []
    String directiveForLine(String line) {

        line.find(/\/\/=(.*)/) { fullMatch, directive -> return directive }
    }
}
