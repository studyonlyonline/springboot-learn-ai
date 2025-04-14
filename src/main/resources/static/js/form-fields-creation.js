const createTextInput = (listName, rowIndex, fieldName, fieldInputType) => {
	const input = document.createElement('input');

	if (fieldName == null || fieldName == undefined) {
		input.id = listName + rowIndex;
		input.setAttribute('name', listName + '[' + rowIndex + ']');
		input.setAttribute("type", fieldInputType)
	} else {
		input.id = listName + rowIndex + '.' + fieldName;
		input.setAttribute('name', listName + '[' + rowIndex + '].' + fieldName);
		input.setAttribute('type', fieldInputType);
	}
	input.setAttribute("class", "form-control");
	input.setAttribute("size", "100");
	return input;
}

const createTextAreaInput = (listName, rowIndex, fieldName, fieldInputType) => {
	const input = document.createElement('textarea');

	if (fieldName == null || fieldName == undefined) {
		input.id = listName + rowIndex;
		input.setAttribute('name', listName + '[' + rowIndex + ']');
		input.setAttribute("type", fieldInputType)
	} else {
		input.id = listName + rowIndex + '.' + fieldName;
		input.setAttribute('name', listName + '[' + rowIndex + '].' + fieldName);
		input.setAttribute('type', fieldInputType);
	}
	input.setAttribute("class", "form-control");
	input.setAttribute("rows", "4");
	input.setAttribute("cols", "100");
	return input;
}

const createCheckboxTypeInput = (listName, rowIndex, fieldName) => {

	// expected output
	// <input class="form-check-input" type="checkbox" checked="checked" id="attributes0.selected1" name="attributes[0].selected" value="true">

	const input = document.createElement("input");

	input.setAttribute("class", "form-check-input");
	input.setAttribute("type", "checkbox");
	input.setAttribute("checked", "checked");
	input.setAttribute("name", listName + "[" + rowIndex + "]" + "." + fieldName);
	input.setAttribute("id", listName + rowIndex + "." + fieldName + "1");

	console.log("created input " + input);
	return input;
}

const createCheckboxTypeInputWithHiddenField = (listName, rowIndex, fieldName) => {

	// expected output
	// <input type="hidden" name="_attributes[0].selected" value="on"/>

	const input = document.createElement("input");

	input.setAttribute("type", "hidden");
	input.setAttribute("value", "on");
	input.setAttribute("name", "_" + listName + "[" + rowIndex + "]" + "." + fieldName);

	console.log("created input with hidden" + input);
	return input;
}

const createLabel = (labelName, rowIndex) => {
	const label = document.createElement('label');
	label.textContent = labelName + " " + rowIndex;
	return label;
}

const createSelectField = (attributesListName, attributesFieldCount, fieldName, listOfOptions, selectedPosInDropdown) => {
	const attributes = listOfOptions;
	console.log("attributes -> " + attributes);

	const select = document.createElement("select");
	select.setAttribute("name", attributesListName + "[" + attributesFieldCount + "]." + fieldName);
	for (let x=0; x<attributes.length; x++) {
		const newOption = document.createElement("option");
		newOption.setAttribute("value", attributes[x]);
		newOption.text = attributes[x];

		if (x === selectedPosInDropdown) {
			newOption.selected = true;
		}
		select.appendChild(newOption);
	}

	console.log("javascript created => " + select);

	return select;
}

const createField = function (listName, rowIndex, field, defaultSelectedPosInDropdown=0) {

	if (field.fieldHTMLType == 'input' && field.fieldInputType == 'checkbox') {
		let inputField1, inputField2;
		inputField1 = createCheckboxTypeInput(listName, rowIndex, field.fieldName);
		inputField2 = createCheckboxTypeInputWithHiddenField(listName, rowIndex, field.fieldName);
		return [inputField1, inputField2];
	}
	else if (field.fieldHTMLType == 'input') {
		let inputField;
		inputField = createTextInput(listName, rowIndex, field.fieldName, field.fieldInputType);
		return [inputField]
	}
	else if (field.fieldHTMLType == 'textarea') {
		let textAreaField;
		textAreaField = createTextAreaInput(listName, rowIndex, field.fieldName, field.fieldInputType);
		return [textAreaField];
	}
	else if (field.fieldHTMLType == 'select') {
		let selectField = createSelectField(listName, rowIndex, field.fieldName, field.optionsList, defaultSelectedPosInDropdown);
		return [selectField];
	}
};