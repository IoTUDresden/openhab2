/*******************************************************************************
 * ******************************** Requests
 * **************************************
 ******************************************************************************/
var locations;
  
/**
 * First load locations -> then all things -> build the table
 */
function loadSemanticThingsTable() {
	loadLocationsForThings();
}

function loadLocationsForThings() {
	$.ajax("/rest/semantic/extended/locations", {
		method : "GET",
		success : locationsReceived,
		error : handleResponseError
	});
}

function loadThings() {
	$.ajax("/rest/semantic/extended/things", {
		method : "GET",
		success : fillThingsTable,
		error : handleResponseError
	});
}

/*******************************************************************************
 * ******************************** Events
 * ****************************************
 ******************************************************************************/

// selected location changed
$(document).on('change', "select[name='location-select']", function() {
	var value = $(this).find(":selected").data("value");
	alert(value.thing+ "\n" + value.loc);
	// TODO send post to change value
});

/*******************************************************************************
 * ******************************** Helpers
 * ***************************************
 ******************************************************************************/

function locationsReceived(data){
	locations = data;
	loadThings();
} 

// fills the options in the Things table with the locations
function fillLocations(data) {
	var selects = document.getElementsByName("location-select");
	for (i = 0; i < selects.length; i++) {
		var lastVal = getLastSelectedValue(selects[i]);
		for (k = 0; k < data.length; k++) {
			if (lastVal != data[k].semanticUri) {
				var opt = createOptionAndSelect(data[k].semanticUri,
						data[k].realLocationName, false);
				selects[i].appendChild(opt);
			}
		}
	}
}

// gets the value from the option with attribute 'selected'
function getLastSelectedValue(select) {
	for (j = 0; j < select.children.length; j++) {
		if (select.children[j].hasAttribute("selected")) {
			return select.children[j].getAttribute("value");
		}
	}
	return " ";
}

// fills the things table
function fillThingsTable(data) {
	var tBody = document.getElementById("things_table_body");
	removeAllChilds(tBody);
	for ( var i = 0; i < data.length; i++) {
		var obj = data[i];
		var row = document.createElement('tr');
		if (!addedEmptyRow("openHabName", obj, row)) {
			addValueToRow(row, obj.openHabName);
		}

		if (!addedEmptyRow("clazz", obj, row)) {
			var split = obj.clazz.split("#");
			addValueToRow(row, split[1]);
		}

		addLocationSelect(row, obj)

		if ("poi" in obj && "orientation" in obj.poi && "position" in obj.poi) {
			addValueToRow(row, "P: " + obj.poi.position + " O: "
					+ obj.poi.orientation);
		} else {
			addValueToRow(row, "");
		}

		addThingPoiBtn(obj.semanticUri, row);
		tBody.appendChild(row);
	}
}

function addLocationSelect(row, obj) {
	var cell = document.createElement('td');
	var sel = document.createElement('select');
	var curLoc = "";
	sel.setAttribute("class", "form-control");
	sel.setAttribute("name", "location-select");

	if ("location" in obj && "semanticUri" in obj.location) {
		curLoc = obj.location.semanticUri;
	}
	
	//empty select for deleting
	var opt = createOptionAndSelect(obj.semanticUri, "", " ", curLoc == "");
	sel.appendChild(opt);
	
	for(k = 0; k < locations.length; k++){
		var select = locations[k].semanticUri == curLoc;
		opt = createOptionAndSelect(obj.semanticUri, locations[k].semanticUri, locations[k].realLocationName, select);
		sel.appendChild(opt);
	}

	cell.appendChild(sel);
	row.appendChild(cell);
}

// select = true -> selected entry
function createOptionAndSelect(thingUri, locUri, text, select) {
	var opt = document.createElement('option');
	var txt = document.createTextNode(text);
	var data = {thing: thingUri, loc: locUri};	

	opt.setAttribute("data-value", JSON.stringify(data));
	if (select) {
		opt.setAttribute("selected", "");
	}

	opt.appendChild(txt);
	return opt;
}

function addThingPoiBtn(btnValue, row) {
	var cell = document.createElement('td');
	var setBtn = document.createElement('button');
	var node = document.createTextNode("set to current");

	setBtn.setAttribute("class", "btn btn-sm btn-primary");
	setBtn.setAttribute("value", btnValue);
	setBtn.appendChild(node);

	var delBtn = document.createElement('button');
	node = document.createTextNode("delete");
	delBtn.setAttribute("class", "btn btn-sm btn-danger");
	delBtn.setAttribute("value", btnValue);
	delBtn.appendChild(node);

	cell.appendChild(setBtn);
	cell.appendChild(delBtn);
	row.appendChild(cell);
}

function addedEmptyRowFromSub(property, sub, data, row) {
	if (property in data) {
		return false;
	}
	addValueToRow(row, "");
	return true;
}

function addedEmptyRow(property, data, row) {
	if (property in data) {
		return false;
	}
	addValueToRow(row, "");
	return true;
}

function addValueToRow(row, value) {
	var cell = document.createElement('td');
	var cont = document.createTextNode(value);
	cell.appendChild(cont);
	row.appendChild(cell);
}

// removes all childs from the parent
function removeAllChilds(root) {
	while (root.firstChild)
		root.removeChild(root.firstChild);
}

function handleResponseError(jqXHR, textStatus, errorThrown) {
	console.log("Error: " + errorThrown + ": " + jqXHR.responseText);
}