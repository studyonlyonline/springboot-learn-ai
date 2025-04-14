const handleLeadForm = event => {
    event.preventDefault();

    const leadForm = document.forms.namedItem("lead-form");
    let output = {};
    for (let i = 0; i < leadForm.length - 1; i++) {
        output[leadForm[i]['name']] = leadForm[i]['value'];
        leadForm[i]['value'] = null;
    }

    const eventLabel = output['pageType'];
    if (eventLabel) {
        sendGAEvent("ev-enquiry-static-form-submit-click",
            "ev-enquiry-form-submission",
            "ev-product-" + eventLabel);
    } else {
        sendGAEvent("ev-enquiry-static-form-submit-click",
            "ev-enquiry-form-submission",
            "ev-product-unidentified");
    }

    submitLeadForm(JSON.stringify(output));
};

const removeData = function () {

    const requestCallbackText = document.getElementById("request-callback-text");
    requestCallbackText.classList.remove("d-flex");
    requestCallbackText.classList.remove("align-items-center");
    requestCallbackText.classList.remove("justify-content-center");
    requestCallbackText.classList.add("hide-lead-form");

    const requestCallbackForm = document.getElementById("request-callback-form");
    requestCallbackForm.classList.remove("d-flex");
    requestCallbackForm.classList.remove("align-items-center");
    requestCallbackForm.classList.remove("justify-content-center");
    requestCallbackForm.classList.add("hide-lead-form");

    const completeLeadForm = document.getElementById("complete-lead-form");
    completeLeadForm.classList.add("d-flex");
    completeLeadForm.classList.add("align-items-center");
    completeLeadForm.classList.add("justify-content-center");

    const loaderSign = document.getElementById("process-lead-form");
    loaderSign.style.display = "initial";
}

function submitLeadForm(output) {
    const requestURL = window.location.origin + "/lead-form";
    anyAjaxRequest(output, requestURL, removeData, function (responseText) {

        const loaderSign = document.getElementById("process-lead-form");
        loaderSign.style.display = "none";
        if("success".localeCompare(String(responseText)) == 0) {
            const successStatus = document.getElementById("successfully-submitted-lead-form");
            successStatus.style.display = "initial";

            //send google analytics event for successful submission
            sendGAEvent("ajax-form-submit-response",
                "ev-enquiry-form-submit-status",
                "ev-enquiry-submit-success");

        } else {
            const failureStatus = document.getElementById("failure-submitted-lead-form");
            failureStatus.style.display = "initial";

            //send google analytics event for successful submission
            sendGAEvent("ajax-form-submit-response",
                "ev-enquiry-form-submit-status",
                "ev-enquiry-submit-failure");
        }
    });
}

function anyAjaxRequest(formData, requestURL, removeData, leadFormCallback) {
    var request = new XMLHttpRequest();
    request.open("POST", requestURL);
    request.setRequestHeader("Content-Type", "application/json;charset=utf8")

    request.onreadystatechange = function () {
        if (this.readyState === 4 && this.status === 200) {
            leadFormCallback(this.responseText);
        } else if(this.readyState == 4 && this.state != 200) {
            leadFormCallback("error");
        } else {
            const loaderSign = document.getElementById("process-lead-form");
            loaderSign.style.display = "initial";
        }
    }

    request.send(formData);
    removeData();
}

const sendGAEvent = (eventAction, eventCategory, eventlabel) => {

    gtag('event', eventAction, {
        'event_category': eventCategory,
        'event_label': eventlabel,
    });
}

document.forms.namedItem("lead-form").addEventListener('submit', handleLeadForm);

