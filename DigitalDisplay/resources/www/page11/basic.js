$(document).ready(function () {
    $.getJSON("../table_json.json",
    function (json) {

        $.each( json, function( table,tval ) {

            if(table=='table3'){
                $.each(tval,function(key,val){
                    var myRow = "<tr></tr>";
                    $("#total_booking tr:last").after(myRow);
                    $.each(val,function(tr,td){
                        var mytdata = "<td>"+td+"</td>";
                        $("#total_booking tr:last").append(mytdata);
                    });
                });

            }

        });
    });

});