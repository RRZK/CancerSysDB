/*
package de.cancersysdb

import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.AnnotationNode
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.transform.ASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation

*/
/**
 * Created by rkrempel on 19-5-17.
 *//*


@GroovyASTTransformation(phase = CompilePhase.CANONICALIZATION)
public class DataSetDataRegistrator implements ASTTransformation{
    ClassNode dataset
    List<ClassNode> toregister = []
    void visit(ASTNode[] astNodes, SourceUnit sourceUnit) {
        if (!isValidAstNodes(astNodes))
            return

        ClassNode cnode = astNodes[0]

        String pack = cnode.getPackageName()
        String name = cnode.getName()
        if(name.equals(Dataset.name) && pack.equals(Dataset.package.name) ){
            dataset = cnode
            dataset.addProperty("hasMany",)
            dataset.getField("hasMany")
            dataset.removeField("hasMany")

        }*/
/*else if( pack.equals(Dataset.package.name)&& name.startsWith("Data") && ! name.endsWith("Annotation")){

            toregister.add(cnode)*//*

        }

    }


    private isValidAstNodes(ASTNode[] astNodes) {
        return astNodes != null && astNodes[0] != null && astNodes[1] != null && astNodes[0] instanceof AnnotationNode && astNodes[0].classNode?.name == I18nFields.class.getName() && astNodes[1] instanceof ClassNode


    }


}
*/
