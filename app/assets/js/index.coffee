loadLocations = (select, TownId) ->
	$ ->
	  $("#"+select).empty()
	  $.get "/listLocations/"+TownId, (data) ->
	    $.each data, (index, item) ->
	      $("#"+select).append "<option value="+item.id+">" + item.name + "</option>"
	