const displayContent = (id, toDisplay = false, displayProperty = "block") => {
	if (toDisplay) {
		document.getElementById(id).style.display = displayProperty;
	} else {
		document.getElementById(id).style.display = "none";
	}
}

function submitEnquiryForm(output) {
	// console.log("leadform data to submit" + output);
	const requestURL = window.location.protocol + "//" + window.location.host + "/enquiry-form-submit";

	//hide form
	displayContent("enquiry-lead-form", false);

	//show loader
	displayContent("loader-sign", true);

	anyAjaxRequest(output, requestURL, function (responseText) {

		if ("success".localeCompare(String(responseText)) == 0) {

			//hide loader
			displayContent("loader-sign", false);

			//hide failure
			displayContent("failure-message", false);

			//show success
			displayContent("successful-message", true);

			//remove remember me
			displayContent("remember-me-checkbox", false);

			//remove submit button
			displayContent("enquiry-submit-button", false);

			//send google analytics event for successful submission
			sendGAEvent("ajax-form-submit-response",
				"enquiry-form-submit-status",
				"enquiry-form-submit-success");

		} else {
			//hide loader
			displayContent("loader-sign", false);

			//show failure
			displayContent("failure-message", true);

			//hide success
			displayContent("successful-message", false);

			//remove remember me
			displayContent("remember-me-checkbox", false);

			//remove submit button
			displayContent("enquiry-submit-button", false);

			//send google analytics event for failure submission
			sendGAEvent("ajax-form-submit-response",
				"enquiry-form-submit-status",
				"enquiry-form-submit-failure");
		}
	});
}

function anyAjaxRequest(formData, requestURL, callBackFunction) {
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
		// } else if (){
		// 	callBackFunction("error");
		// }
	}

	request.send(formData);
	// removeData();
}

const setCookieDataInEnquireNowForm = (responseText) => {
	// console.log("response cookie data" + responseText);
	const responseObj = JSON.parse(responseText);
	// console.log("name hash " + responseObj.nameHash);
	document.getElementById('nameHash').value = responseObj.nameHash;
	document.getElementById('phoneHash').value = responseObj.phoneHash;

	if (document.getElementById('enquiredCityHash') != null
		&& document.getElementById('enquiredCityHash') != undefined) {
		document.getElementById('enquiredCityHash').value = responseObj.enquiredCityHash;
	}
}

const sendGAEvent = (eventAction, eventCategory, eventlabel) => {

	gtag('event', eventAction, {
		'event_category': eventCategory,
		'event_label': eventlabel,
	});
}

const handleEnquiryForm = event => {
	event.preventDefault();
	// console.log("handle enquiry form begin");
	const leadForm = document.forms.namedItem("enquiry-lead-form");
	let output = {};
	// console.log("form length " + leadForm.length);
	for (let i = 0; i < leadForm.length; i++) {
		output[leadForm[i]['name']] = leadForm[i]['value'];
		// console.log("key = " + leadForm[i]['name'] + " value = " + leadForm[i]['value']);
		leadForm[i]['value'] = null;
	}
	output['rememberMeHash'] = document.getElementById("rememberMeHash").checked;

	//send google event
	const eventLabel = output['productIdHash'];
	if (eventLabel) {
		sendGAEvent("click-enquiry-form-submit",
			"product-listing-EnquiryFormSubmission",
						"product-id-" + eventLabel);
	} else {
		sendGAEvent("click-enquiry-form-submit",
			"product-listing-EnquiryFormSubmission",
			"undefined-product-id");
	}

	submitEnquiryForm(JSON.stringify(output));
};

const resetEnquiryForm = () => {
	const leadForm = document.forms.namedItem("enquiry-lead-form");
	for (let i = 0; i < leadForm.length; i++) {
		leadForm[i]['value'] = null;
	}
	document.getElementById("rememberMeHash").checked = true;
}

const populateMessageBodyWithProductType = (productId, productName) => {
	const messageBody = "Hi, I want to enquire about " + productName;
	document.getElementById('messageHash').value = messageBody;
	document.getElementById('productIdEnquiryFormHash').value = productId;
}

const populateMessageBodyWithSubmittedMessage = (message) => {
	const messageBody = message;
	document.getElementById('messageHash').value = messageBody;
}

const decodeCookieData = (productId, productName, message = undefined) => {

	// console.log("inside decodeCookieData");
	// console.log("Product id " + productId);
	// console.log("Product name " + productName);

	//hide loader
	displayContent("loader-sign", false);
	displayContent("successful-message", false);
	displayContent("enquiry-lead-form", true);
	displayContent("failure-message", false);
	//remove remember me
	displayContent("remember-me-checkbox", true);
	//remove submit button
	displayContent("enquiry-submit-button", true, "initial");

	resetEnquiryForm();

	const requestURL = window.location.protocol + "//" + window.location.host + "/decode";

	if (message != null && message != undefined) {
		populateMessageBodyWithSubmittedMessage(message)
	} else {
		populateMessageBodyWithProductType(productId, productName);
	}

	// console.log($.cookie("fdata"));
	const fdata = $.cookie("fdata");
	// console.log("fData " + fdata);
	if (fdata == null || fdata === undefined) {
		return;
	}

	const cookieData = {};
	cookieData['decode'] = fdata;
	anyAjaxRequest(JSON.stringify(cookieData), requestURL, setCookieDataInEnquireNowForm);
}

document.getElementById("enquiry-submit-button").addEventListener('click', handleEnquiryForm);
// document.getElementById("enquireNow").addEventListener('click', decodeCookieData);

