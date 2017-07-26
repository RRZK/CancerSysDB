/**
 * Created by rkrempel on 30.07.15.
 */

var GenetipTipdata = {};

function showGeneTip(chromosome,startPos,endPos,theID) {
    var selector = "#"+theID;

    if($(selector).attr("data-hasqtip"))
        return ;

    idTipData = "" + chromosome + startPos + endPos;
    var gt = GenetipTipdata[idTipData];

    //Object.keys(GenetipTipdata);





        $(selector).qtip({
            content: {
                text:"Loading.."
            }
            ,/*
            show: {solo: true, delay: 100 , event: "mouseenter"},
            hide: {delay: 1000,event: "mouseout" },
            style: {classes: 'qtip-light qtip-rounded'},*/
            position: {
                target: 'mouse', // Track the mouse as the positioning target
                adjust: { x: 5, y: 5 } // Offset it slightly from under the mouse

/*                my: 'top left',
                at: 'bottom right'*/

            }});
    if(!gt){
        $.get('../../Gene/getByPosition', {
                    chromosome: chromosome, startPos: startPos, endPos: endPos
                }
            ).then(function (content) {
                    GenetipTipdata[idTipData] = content;
                    $(selector).qtip('option', 'content.text', content);

            });
        }else{

            $(selector).qtip('option', 'content.text', gt);
        }
/*        $(this).qtip({
            content: {
                text:
            },
            show: {solo: true, delay: 100, event: "mouseenter"},
            hide: {delay: 100, event: "mouseleave"},
            style: {classes: 'qtip-light qtip-rounded'},
            position: {
                my: 'top left',
                at: 'bottom right',
                /!*                target: 'mouse', // Track the mouse as the positioning target
                 adjust: {
                 // Don't adjust continuously the mouse, just use initial position
                 mouse: false,
                 x: 4,
                 y: 4

                 }*!/

            }*/


        //$( "#"+id ).tooltip( "option", "enabled" );


}
/*
function GenetipGetInfo(idTipData,chromosome,startPos,endPos){

                $.get('../../Gene/getByPosition', {
                    chromosome:chromosome,startPos:startPos,endPos:endPos
                },
                function(data){GenetipTipdata[idTipData] = data;}

                )
}
*/
