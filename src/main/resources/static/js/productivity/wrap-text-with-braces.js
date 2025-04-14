window.addEventListener("keydown", function(e){
    var selectedText = getSelectionText();
    if(e.keyCode === 16 && selectedText != "" && e.ctrlKey) {
        e.preventDefault();
        replaceSelectedText(modifyStringWithCurlyBraces(selectedText));
    }
});

function getSelectionText() {
    var text = "";
    if (window.getSelection) {
        text = window.getSelection().toString();
    } else if (document.selection && document.selection.type != "Control") {
        text = document.selection.createRange().text;
    }
    return text;
}

function modifyStringWithCurlyBraces(givenString) {
    var b = '';
    var a = givenString;
    b = '{{{' + givenString + '}}}';
    return b;
}

function replaceSelectedText(text) {
    var txtArea = document.activeElement;
    console.log(txtArea);
    if (txtArea.selectionStart != undefined) {
        var startPos = txtArea.selectionStart;
        var endPos = txtArea.selectionEnd;
        selectedText = txtArea.value.substring(startPos, endPos);
        txtArea.value = txtArea.value.slice(0, startPos) + text + txtArea.value.slice(endPos);
    }
}