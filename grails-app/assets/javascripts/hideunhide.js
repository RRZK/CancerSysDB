function hideUnhide(buttonelementId,HideUnhideId, postfix){
    postfix = " "+postfix.trim();
    if($("#"+HideUnhideId).is(":hidden") ){

        $("#"+HideUnhideId).show()
        $("#"+buttonelementId).text('hide'+postfix);
    }else{

        $("#"+HideUnhideId).hide()
        $("#"+buttonelementId).text('show'+postfix);
    }


}