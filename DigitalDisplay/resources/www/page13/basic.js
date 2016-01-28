$(document).ready(function () {
    $.getJSON("../table_json.json",
    function (json) {

        $.each( json, function( table,tval ) {

            if(table=='table0'){
                $.each(tval,function(key,val){
                    var myRow = "<tr></tr>";
                    $("#racetimes tr:last").after(myRow);
                    $.each(val,function(tr,td){
                        var mytdata = "<td>"+td+"</td>";
                        $("#racetimes tr:last").append(mytdata);
                    });
                });

            }

            if(table=='table2'){
                $.each(tval,function(key,val){
                    var myRow = "<tr></tr>";
                    $("#new_logos tr:last").after(myRow);
                    $.each(val,function(tr,td){
                        var mytdata = "<td>"+td+"</td>";
                        $("#new_logos tr:last").append(mytdata);
                    });
                });

            }

        });
    });

});