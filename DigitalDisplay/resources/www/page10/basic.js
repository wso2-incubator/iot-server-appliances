$(document).ready(function () {
    $.getJSON("../table_json.json",
    function (json) {

        $.each( json, function( table,tval ) {

            if(table=='table1'){
                $.each(tval,function(key,val){
                    var myRow = "<tr></tr>";
                    $("#racetimes tr:last").after(myRow);
                    $.each(val,function(tr,td){
                        var mytdata = "<td>"+td+"</td>";
                        $("#racetimes tr:last").append(mytdata);
                    });
                });

            }

        });
    });

});