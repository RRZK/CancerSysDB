/**
 *
 */

function fieldDatasetCompleter( datasetFieldSelectorPrefix,FieldPrefix ){



    ds1 = $("#"+datasetFieldSelectorPrefix+"1").val();
    ds2 = $("#"+datasetFieldSelectorPrefix+"2").val();

    if(ds1 =="undefined" || ds1 ==""){

        $("#"+FieldPrefix+"x").attr("disabled",true);
        $("#"+FieldPrefix+"y").attr("disabled",true);
        $("#"+FieldPrefix+"x").empty();
        $("#"+FieldPrefix+"y").empty();
        return

    }

    var str ="";
    if(ds2!=null &&ds2!="null" )
        str = "dataset1="+ds1+"&dataset2="+ds2;
    else
        str = "dataset1="+ds1;
    val1 = $("#"+FieldPrefix+"x").val();
    val2 = $("#"+FieldPrefix+"y").val();



    $("#"+FieldPrefix+"x").attr("disabled",true);
    $("#"+FieldPrefix+"y").attr("disabled",true);
    $("#"+FieldPrefix+"x").empty();
    $("#"+FieldPrefix+"y").empty();

    $.ajax({
        url:'../interactiveScatterplotViz/GetFieldsForDatasets',
        type:'POST',
        data: str,
        dataType: 'json',
        success: function( json ) {
            for (var daSet in json) {
                for (var type in json[daSet]){
                    for(var i in json[daSet][type]){
                        var field = json[daSet][type][i];
                        var FieldDesc= daSet+"_"+type+"_"+field;
                        var opt = $('<option>').text(FieldDesc).attr('value', FieldDesc);

                        if(val1 && FieldDesc == val1)
                            opt.prop('selected', true);
                        $("#"+FieldPrefix+"x").append(opt);


                        var opt = $('<option>').text(FieldDesc).attr('value', FieldDesc);
                        if(val2 && FieldDesc == val2)
                            opt.prop('selected', true);

                        $("#"+FieldPrefix+"y").append(opt);


                    }
                }

            }
        }
    });
    $("#"+FieldPrefix+"x").attr("disabled",false);
    $("#"+FieldPrefix+"y").attr("disabled",false);

}