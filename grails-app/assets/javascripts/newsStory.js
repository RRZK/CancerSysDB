function loadlatestNews(url){


    jQuery.ajax({

        type:'POST',


        url:url,

        success:function(data){

            var remp= jQuery("#NewsStory");

            remp.html(data);
        }
    });


}