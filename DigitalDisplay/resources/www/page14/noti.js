$(document).ready(function () {
    get_data_from_json();


});

function play_music(){
    $("#foobar").trigger('load');
    $("#foobar").trigger('play');
}

function get_data_from_json(){
    $.getJSON("mailing.json",function (json) {
        time=4000;
        i=1;
        subject="";
        from="";
        element="";
        $.each( json, function(index,value ) {


            setTimeout( function(){
                $.each(value,function(key,val){

                    if(key=="subject"){
                        subject="<h1 align='center'>"+val+"</h1>";
                    }

                    if(key=="from"){
                        from="<h1 align='center'><span id='from'>From :<span id='sub'> "
                        from+="<span id=from_con>"+val+"</span></h1>";
                    }
                    if(key=="notification"){
                        $.each(val,function(array_index,array_val){
                            element+="<h2 align='center'>"+array_val+"</h2>";
                            $("#notifications").append(element);
                        });
                    }

                    if(i>=3){
                        $("#notifications").html(subject+element+from);
                        element="";
                        subject="";
                        from="";
                        i=0
                        play_music();

                    }


                    i+=1;

                });
            }, time)
            time+=52000;


        });
    });
}

