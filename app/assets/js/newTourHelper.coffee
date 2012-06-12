loadLocations = (selectObject, TownId) ->
	$ ->
	  $("#"+selectObject).empty()
	  $.get "/listLocations/"+TownId, (data) ->
	    $.each data, (index, item) ->
	      $("#"+selectObject).append "<option value="+item.id+">" + item.name + "</option>"

setSelected = (NextSelectObject, RectObject, Value) ->
   if typeof(Value) == "undefined"
      $("#"+NextSelectObject).hide()
      $('#'+RectObject).removeClass("bar_active")
      $('#'+RectObject).addClass("bar_deactive")
      return false
   else
      $("#"+NextSelectObject).show()
      $('#'+RectObject).removeClass("bar_deactive")
      $('#'+RectObject).addClass("bar_active")
      return true
      
`findDepLocations = function(TownIdValue){
   if (!(typeof(TownIdValue) == "undefined")){
      loadLocations('dep_location',TownIdValue); 
      loadLocations('arr_location',TownIdValue);
         for(i=2;i<=3;i++){
           $('#rect_'+i).removeClass("bar_active");
           $('#rect_'+i).addClass("bar_deactive");
         }
      $('#header').text('where should your taxi pick you up?')
           $('#tourlist').dataTable().fnReloadAjax('http://localhost:9000/listToursByTown/'+TownIdValue);
   }
}

setDepReady = function(LocationIdValue){
   if (!(typeof(LocationIdValue) == "undefined")){
      $('#header').text('where should your taxi bring you to?');
        $('#rect_2').removeClass("bar_deactive");
        $('#rect_2').addClass("bar_active");
            $('#rect_3').removeClass("bar_active");
            $('#rect_3').addClass("bar_deactive");
            $('#tourlist').dataTable().fnReloadAjax('./listToursByDepLocation/'+LocationIdValue);
             $('#tour_container').show();
            $("#arr_location").show();
   }
}

   
setArrReady = function(LocationDepIdValue,LocationArrIdValue){
   if (!(typeof(LocationArrIdValue) == "undefined")){
           $('#rect_3').removeClass("bar_deactive");
           $('#rect_3').addClass("bar_active");
            $('#tourlist').dataTable().fnReloadAjax('./listToursByArrLocation/'+LocationDepIdValue+'/'+LocationArrIdValue);
      }
   }
$(document).ready(function() {
   $.fn.dataTableExt.oApi.fnReloadAjax = function(oSettings, sNewSource)
   {
      
   oSettings.sAjaxSource = sNewSource;
   this.fnClearTable(this);
   this.oApi._fnProcessingDisplay(oSettings, true );
   var that = this;

   $.getJSON(oSettings.sAjaxSource, null, function(json){
   /* Got the data - add it to the table */
   for (var i=0; i<json.aaData.length; i++)
   {
   that.oApi._fnAddData(oSettings, json.aaData[i]);
   }

   oSettings.aiDisplay = oSettings.aiDisplayMaster.slice();
   that.fnDraw(that);
   that.oApi._fnProcessingDisplay(oSettings, false);
   });
   }

   var oTable= $('#activityCodeDataTable').dataTable();
    $('#tourlist').dataTable({
        "bProcessing": true,
        "sAjaxSource": "./listToursByTown/1",
        "aoColumns": [
            { "mDataProp": "dep_location" },
            { "mDataProp": "arr_location" },
            { "mDataProp": "departure" },
            { "mDataProp": "arrival" },
            { "mDataProp": "meetingpoint" },
            {"mDataProp": "comment"}
        ]
    });  
    $.extend( $.fn.dataTableExt.oStdClasses, {
      "sWrapper": "dataTables_wrapper form-inline"
      } );

    });`
