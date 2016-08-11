/********************************************************************************
********************************* Requests **************************************
*********************************************************************************/

function loadSemanticThingsTable() {
	$.ajax("/rest/semantic/extended/things", {
		method : "GET",
		success : fillThingsTable,
		error : handleResponseError
	});
}

function loadLocationsForThings(){
	$.ajax("/rest/semantic/extended/locations", {
		method : "GET",
		success : fillLocations,
		error : handleResponseError
	});	
}


/********************************************************************************
********************************* Helpers ***************************************
*********************************************************************************/
	
//fills the options in the Things table with the locations
function fillLocations(data){
	var selects = document.getElementsByName("location-select");
	for (i = 0; i < selects.length; i++) {
		var lastVal = $(selects[i]).children(':selected').getAttribute('value');
		for (k = 0; k < data.length; k++) {
			if(lastVal != data[k].semanticUri){
				var opt = createOptionAndSelect(data[k].semanticUri, data[k].realLocationName, false);
				selects[i].appendChild(opt);
			}
		}	   
	}	
}

//fills the things table
function fillThingsTable(data){
	var tBody = document.getElementById("things_table_body");
	removeAllChilds(tBody);
	for(var i = 0; i < data.length; i++){
		var obj = data[i];
        var row = document.createElement('tr');
        if(!addedEmptyRow("openHabName", obj, row)){
			addValueToRow(row, obj.openHabName);        	
        }
        
        if(!addedEmptyRow("clazz", obj, row)){
        	var split = obj.clazz.split("#");
			addValueToRow(row, split[1]);
        }
        
        addLocationSelect(row, obj)        
        
        if("poi" in obj && "orientation" in obj.poi && "position" in obj.poi){
			addValueToRow(row, "P: " + obj.poi.position + " O: " + obj.poi.orientation);        	
        }
        else{
        	addValueToRow(row, "");  
        }
        
        addThingPoiBtn("tstValue", row);
        tBody.appendChild(row);
    }
	loadLocationsForThings();
}

function addLocationSelect(row, obj){
    var cell = document.createElement('td');
    var sel = document.createElement('select');
    sel.setAttribute("class", "form-control");
    sel.setAttribute("name", "location-select");
    
	if("location" in obj && "realLocationName" in obj.location && "semanticUri" in obj.location){
		sel.appenChild(createOptionAndSelect(" ", "", false));
		sel.appenChild(createOptionAndSelect(obj.location.semanticUri, obj.location.realLocationName, true));	    
	}
	else{
		sel.appendChild(createOptionAndSelect(" ", "", true));
	}

	cell.appendChild(sel);
	row.appendChild(cell);	
}

// select = true -> selected entry
function createOptionAndSelect(value, text, select){
    var opt = document.createElement('option');
    var txt = document.createTextNode(text);
    opt.setAttribute("value", value);
    if(select){
        opt.setAttribute("selected","");
    }
    
    opt.appendChild(txt);
    return opt;
}

function addThingPoiBtn(btnValue, row){
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

function addedEmptyRowFromSub(property, sub, data, row){
	if(property in data){
		return false;
	}
	addValueToRow(row, "");
	return true;
}

function addedEmptyRow(property, data, row){
	if(property in data){
		return false;
	}
	addValueToRow(row, "");
	return true;
}

function addValueToRow(row, value){
    var cell = document.createElement('td');
    var cont = document.createTextNode(value);
    cell.appendChild(cont);
    row.appendChild(cell);		
}

//removes all childs from the parent
function removeAllChilds(root){
	while (root.firstChild)
		root.removeChild(root.firstChild);	
}

function handleResponseError(jqXHR, textStatus, errorThrown){
	console.log("Error: " + errorThrown + ": " + jqXHR.responseText);	
}