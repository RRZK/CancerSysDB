/**
 *
 * @param SVGFile The Data Element Retrived from the d3.xml() call
 * @param ElementToAppend The Element that Should append the Source of the SVG File
 */

function IntegrateSVGFromFileToPage(SVGFile,ElementToAppend){

    //use plain Javascript to extract the node
    var svg = SVGFile.getElementsByTagName("svg")[0];
    ElementToAppend.node().appendChild(svg);


}