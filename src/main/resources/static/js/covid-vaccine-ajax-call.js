const displayContent = (id, toDisplay = false, displayProperty = "block") => {
	if (toDisplay) {
		document.getElementById(id).style.display = displayProperty;
	} else {
		document.getElementById(id).style.display = "none";
	}
}

function submitVaccineForm(output) {
	const requestURL = window.location.origin + "/covid-vaccine?pincode=" + output['pincodeHash'] + "&age=" + output['age'];

	//show loader
	displayContent("loader-sign", true);

	anyAjaxRequest(requestURL, function (responseText) {

		if ("No-Data".localeCompare(String(responseText)) == 0) {

			hideAllMessages();

			//show failure
			displayContent("failure-message", true);

		} else if("error".localeCompare(String(responseText)) == 0) {
			hideAllMessages();

			//display error
			renderErrorMessage();
		}
		else {

			hideAllMessages();

			//show response
			document.getElementById("vaccine-data").innerHTML = responseText;
		}
	});
}

function anyAjaxRequest(requestURL, callBackFunction) {
	// console.log("ajax call  = " + requestURL);
	var request = new XMLHttpRequest();
	request.open("POST", requestURL);
	request.setRequestHeader("Content-Type", "application/json;charset=utf8");

	request.onreadystatechange = function () {
		// console.log("ready state " + this.readyState);
		// console.log("staus " + this.status);
		if (this.readyState === 4 && this.status === 200) {
			// console.log("response text coming");
			callBackFunction(this.responseText);
		} else if (this.readyState == 4 && this.state != 200) {
			// console.log("error log");
			callBackFunction(this.responseText);
		}
	}

	request.send();
	// removeData();
}


function renderErrorMessage() {
	document.getElementById("vaccine-data").innerHTML = "";

	//hide loader
	displayContent("loader-sign", false);

	//show failure
	displayContent("failure-message", false);

	//show error message
	displayContent("error-message", true);
}

function hideAllMessages() {

	document.getElementById("vaccine-data").innerHTML = "";

	//hide loader
	displayContent("loader-sign", false);

	//show failure
	displayContent("failure-message", false);

	//show error message
	displayContent("error-message", false);
}

const sendGAEvent = (eventAction, eventCategory, eventlabel) => {

	gtag('event', eventAction, {
		'event_category': eventCategory,
		'event_label': eventlabel,
	});
}

const handleVaccineCheckerForm = event => {
	event.preventDefault();
	// console.log("handle enquiry form begin");

	hideAllMessages();

	const leadForm = document.forms.namedItem("vaccine-checker-form");
	let output = {};
	// console.log("form length " + leadForm.length);
	for (let i = 0; i < leadForm.length; i++) {
		output[leadForm[i]['name']] = leadForm[i]['value'];
		// console.log("key = " + leadForm[i]['name'] + " value = " + leadForm[i]['value']);
		leadForm[i]['value'] = null;
	}

	if(document.getElementById('youngAge').checked) {
		output['age'] = 18;
	} else if(document.getElementById('oldAge').checked) {
		output['age'] = 45;
	}

	if (output['namebt'] == null || output['namebt'] == undefined || output['namebt'].length > 0) {
		renderErrorMessage();
	}

	// send google event
	sendGAEvent("click-vaccine-checker-submit",
		"vaccine-checker-form-submission",
		"vaccine-checker");

	submitVaccineForm(output);
};

document.forms.namedItem("vaccine-checker-form").addEventListener('submit', handleVaccineCheckerForm);