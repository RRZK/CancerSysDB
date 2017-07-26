function completeData( name,  fieldChanged, valueOfField   ){

    var StudyFieldID = "Study"+name;
    var PatientFieldID = "Patient"+name;
    var SampleFieldID = "Sample"+name;


    var changed = fieldChanged+name;
    if(typeof valueOfField === 'undefined')
    valueOfField=$("#"+changed).val();

    var para = "";
    switch (fieldChanged){

        case "Patient":

            para ="?Patient="+valueOfField;
        break;
        case "Study":
            para ="?Study="+valueOfField;
        break;
        case "Sample":
            para ="?Sample="+valueOfField;
            break;
        case "Dataset":
            para ="?Dataset="+valueOfField;
            break;
    }


    $.ajax({
        url:'../viz/DataSelectSuggest'+para,
        type:'POST',
        dataType: 'json',
        success: function( json ) {
            FillData(StudyFieldID,json["Studies"]);

            FillData(PatientFieldID,json["Patients"]);

            FillData(SampleFieldID,json["Samples"]);
            if(json["Studies"]["Active"]!= ""){
                $("#"+StudyFieldID).select2().val(json["Studies"]["Active"]["id"]);
                $("#"+StudyFieldID).trigger('change');

            if(json["Patients"]["Active"]!= ""){
                $("#"+PatientFieldID).select2().val(json["Patients"]["Active"]["id"]);
                $("#"+PatientFieldID).trigger('change');
            }

            }
            if(json["Samples"]["Active"]!= ""){
                $("#"+SampleFieldID).select2().val(json["Samples"]["Active"]["id"]);
                $("#"+SampleFieldID).trigger('change');
            }

            if(json["Datasets"]!= undefined &&json["Datasets"]!= ""){
                $("#"+name+"DatasetChooser").show();
                if(json["Datasets"].length == 0){
                    $("#"+name+"DatasetChooserLabel").text( "No Dataset availible" );

                }else if(json["Datasets"].length == 1){


                    $("#"+name+"DatasetChooserLabel").text( json["Datasets"][0]["text"] );


                    var hiddenfield = $( "#"+name );

                    hiddenfield.attr("value",json["Datasets"][0]["id"] );
                    hiddenfield.change();

                }else{

                    //TODO implement the Many Collections Filter
                    $("#"+name+"DatasetChooserLabel").text( "TODO Many Results Possible Results NOT IMPLEMENTED JET !");


                }



            }else {
                $("#"+name+"DatasetChooser").hide();

            }

        }
    });



    //de.cancersysdb.Study.list()

    //de.cancersysdb.Patient.list()
    //de.cancersysdb.Sample.list()

    //TODO:
    // 1. GET Value from Field
    // 2. GET Possible Values for later Fields
    // 3. SET possible Values for later Field
    // 4. IF Sampe is Chosen Fill Hidden field with ID
    // 5. IF Sample is Chosen check OKAY



}

function FillData( fieldID,datachunk,activeDataset){


    var data= new Array();

    data.push({ id: "null", text: 'Choose' });




    $("#"+fieldID)
        .find('option')
        .remove()
        .end();
    $("#"+fieldID)
        .find('optgroup')
        .remove()
        .end();
    if(datachunk["Own"] && datachunk["Own"] !="")
        data.push({text:"My Data", children:datachunk["Own"] });

    if(datachunk["Accessable"] && datachunk["Accessable"] != "")
        data.push({text:"Availible Data", children:datachunk["Accessable"]});
    $("#"+fieldID).select2(
       "destroy"
    );

    $("#"+fieldID).select2({
        data: data
    });

    if(datachunk["Active"] !="-1" ){
        $("#"+fieldID).val(datachunk["Active"]);
    }else
        $("#"+fieldID).val("null");

}