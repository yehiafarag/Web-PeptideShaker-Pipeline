
// Define the namespace
var mylibrary = mylibrary || {};
mylibrary.SelectioncanvasComponent = function (element) {
    element.innerHTML =
            "<canvas id='selectioncanvas' oncontextmenu='return false;'></canvas>" +
            "<input id='coords' type='text' name='value'/>" +
            "<input id='tbox' type='text' name='value'/>" +
            "<input id='hiddenbtn' type='button' value='Click'/>" +
            "<img id='leftarrow' width='32' height='32' src='VAADIN/themes/webpeptideshakertheme/img/vaadin-arrow-left.svg' alt=''>" +
            "<img id='rightarrow' width='32' height='32' src='VAADIN/themes/webpeptideshakertheme/img/vaadin-arrow-right.svg' alt=''>";
    var document = element.ownerDocument;
    // Style it
    // Getter and setter for the value property
    this.getValue = function () {
        return element.getElementsByTagName("input")[0].value;
    };
    this.setValue = function (value) {
        var res = value.split(",");
        if (res.length === 2) {
            theCanvas.width = parseInt(res[0], 10);
            theCanvas.height = parseInt(res[1], 10);
        }
    };
    // Default implementation of the click handler
    this.click = function () {
        alert("Error: Must implement click() method");
    };
    // Set up button click
    var button = element.getElementsByTagName("input")[1];
    var self = this; // Can't use this inside the function

    button.onclick = function () {
        self.click();
        button.disabled = true;
        setTimeout(activateBtn, 1000);
    };
    function activateBtn() {
        button.disabled = false;
    }





    var thecoordsText = document.getElementById('coords');
    var tBox = document.getElementById('tbox');
    var theCanvas = document.getElementById('selectioncanvas');
    var ctx = theCanvas.getContext('2d');
    var drawLine = false;
    var mouseevent = false;
    var touchevent = false;
    var finalPos = {x: -1, y: -1};
    var startPos = {x: 0, y: 0};
    var finalTouchI = {x: -1, y: -1};
    var startTouchI = {x: -1, y: -1};
    var finalTouchII = {x: -1, y: -1};
    var startTouchII = {x: -1, y: -1}
    var btntype = "-500";
    var cs = getComputedStyle(theCanvas);
/// these will return dimensions in *pixel* regardless of what
/// you originally specified for image:
    var width = parseInt(cs.getPropertyValue('width'), 10);
    var height = parseInt(cs.getPropertyValue('height'), 10);
/// now use this as width and height for your canvas element:
    theCanvas.width = width;
    theCanvas.height = height;
    thecoordsText.value = width + "," + height;
    var canvasOffset = $('#selectioncanvas').offset();
    var leftarrow = document.getElementById("leftarrow");
    var rightarrow = document.getElementById("rightarrow");
    var selectionCanvasElement = $('#selectioncanvas');

    function line(cnvs) {
        clearCanvas();
        cnvs.beginPath();
        cnvs.moveTo(startPos.x, startPos.y);
        cnvs.lineTo(finalPos.x, startPos.y);
        cnvs.stroke();
    }

    function rect(cnvs) {
        clearCanvas();
        cnvs.beginPath();
        //draw left arraw       
        if (finalTouchI.x < startTouchI.x) {
            ctx.drawImage(leftarrow, finalTouchI.x, startTouchI.y - 8, 16, 16);
        } else {
            ctx.drawImage(rightarrow, finalTouchI.x, startTouchI.y - 8, 16, 16);
        }
        cnvs.moveTo(startTouchI.x, startTouchI.y);
        cnvs.lineTo(finalTouchI.x + 10, startTouchI.y);
        if (finalTouchII.x < startTouchII.x) {
            ctx.drawImage(leftarrow, finalTouchII.x, startTouchII.y - 8, 16, 16);
        } else {
            ctx.drawImage(rightarrow, finalTouchII.x, startTouchII.y - 8, 16, 16);
        }
        cnvs.moveTo(startTouchII.x, startTouchII.y);
        cnvs.lineTo(finalTouchII.x + 10, startTouchII.y);
        cnvs.stroke();
    }

    function clearCanvas() {
        ctx.clearRect(0, 0, theCanvas.width, theCanvas.height);
    }

    $('#selectioncanvas').mousemove(function (e) {
        if (touchevent)
            return;
        if (drawLine === true && mouseevent) {
            finalPos = {x: e.pageX - canvasOffset.left, y: startPos.y};
            clearCanvas();
            line(ctx);
        }
    });

    $('#selectioncanvas').mousedown(function (e) {
        if (touchevent)
            return;
        btntype = e.which;
        mouseevent = true;
        drawLine = true;
        ctx.strokeStyle = 'black';
        ctx.lineWidth = 1;
        ctx.lineCap = 'round';
        ctx.beginPath();
        startPos = {x: e.pageX - canvasOffset.left, y: e.pageY - canvasOffset.top};

    });

    $('#selectioncanvas').mouseup(function () {
        if (touchevent)
            return;
        mouseevent = false;
        clearCanvas();
        // Replace with var that is second canvas
        thecoordsText.value = startPos.x + "," + startPos.y + "," + finalPos.x + "," + finalPos.y + "," + btntype;
        line(ctx);
        finalPos = {x: -1, y: -1};
        startPos = {x: -1, y: -1};
        drawLine = false;
        ctx.beginPath();
        clearCanvas();
        document.dis;
        button.click(btntype);
        btntype = "-500";
    });



    selectionCanvasElement.touch({preventDefault: {
            drag: true,
            swipe: true,
            tap: true,
            panzoom: true,
            contextmenu: true
        }});

    selectionCanvasElement.on('doubleTap', function (e) {
        touchevent = true;
        zoomout();
        touchevent = false;
    });

    selectionCanvasElement.on('touchstart', function (ev) {
        if (mouseevent)
            return;
        touchevent = true;
        if (ev.originalEvent.touches.length !== 2)
            return;
        ctx.strokeStyle = '#626262';
        ctx.lineWidth = 2;
        ctx.lineCap = 'round';
        ctx.beginPath();
        clearCanvas();
        startTouchI = {x: parseInt(ev.originalEvent.touches[0].pageX - canvasOffset.left, 10), y: parseInt(ev.originalEvent.touches[0].pageY - canvasOffset.top, 10)};
        startTouchII = {x: parseInt(ev.originalEvent.touches[1].pageX - canvasOffset.left, 10), y: parseInt(ev.originalEvent.touches[1].pageY - canvasOffset.top, 10)};
    });

    selectionCanvasElement.on('touchend', function (ev) {
        if (mouseevent) {
            return;
        }
        setTimeout(touchEnd, 500);
        ctx.beginPath();
        clearCanvas();
        document.dis;
    });
    function zoomout() {
        startPos.x = -1;
        startPos.y = -1;
        finalPos.x = -1;
        finalPos.y = -1;
        thecoordsText.value = startPos.x + "," + startPos.y + "," + finalPos.x + "," + finalPos.y + "," + 2;
        button.click(2);
    }
    function  touchEnd() {

        //get direction of each touch
        var firstTouchToLeft = false;
        var secoundTouchToLeft = false;
        var firstTouchDistance = -1;
        var secoundTouchDistance = -1;
        if (startTouchI.x > finalTouchI.x) {
            firstTouchToLeft = true;
        }
        if (startTouchII.x > finalTouchII.x) {
            secoundTouchToLeft = true;
        }
        firstTouchDistance = (startTouchI.x - finalTouchI.x) / width;
        secoundTouchDistance = (startTouchII.x - finalTouchII.x) / width;
        if (firstTouchToLeft === secoundTouchToLeft) {
            if (firstTouchToLeft) {
                //zoom in mainly from left to right
                var min = Math.min(startTouchI.x, startTouchII.x);
                var max = Math.max(finalTouchI.x, finalTouchII.x);

            }
        } else if (firstTouchToLeft !== secoundTouchToLeft) {

            var zoomIn = false;
            if ((firstTouchToLeft && firstTouchDistance > 0 && secoundTouchDistance <= 0)) {
                if (startTouchI.x <= startTouchII.x) {

                    //to test 
                    //
                    startPos.x = finalTouchI.x;
                    startPos.y = 100;
                    finalPos.x = finalTouchII.x;
                    finalPos.y = 100;

                    //
                    //
                    //
//                    zoomIn = true;
//                    var leftchanging = startTouchI.x - (startTouchI.x - finalTouchI.x) - (startTouchI.x / 10);
//                    startPos.x = Math.max(startTouchI.x - ((leftchanging)), 0);
//                    startPos.y = 100;
//                    var rightdistance = finalTouchII.x - startTouchII.x;
//                    finalPos.x = Math.min(startTouchII.x + (theCanvas.width - startTouchII.x - rightdistance), theCanvas.width);
//                    finalPos.y = 1000;
//                    tBox.value = startPos.x + "---" + startTouchI.x + "  " + startTouchII.x + "  ---First-->  " + finalPos.x;
                } else {
                    zoomout();
                }
            } else if ((secoundTouchToLeft && secoundTouchDistance > 0 && firstTouchDistance <= 0)) {
                if (startTouchI.x >= startTouchII.x) {
                    //to test 
                    //
                    startPos.x = finalTouchII.x;
                    startPos.y = 100;
                    finalPos.x = finalTouchI.x;
                    finalPos.y = 100;

                    //
                    //
                    //
//                    zoomIn = true;
//                    var leftchanging = startTouchII.x - (startTouchII.x - finalTouchII.x) - (startTouchII.x / 10);
//                    startPos.x = Math.max(startTouchII.x - leftchanging, 0);
//                    startPos.y = 100;
//                    var rightdistance = (finalTouchI.x) - startTouchI.x;
//                    finalPos.x = Math.min(startTouchI.x + (theCanvas.width - startTouchI.x - rightdistance), theCanvas.width);
//                    finalPos.y = 1000;
//                    tBox.value = startPos.x + "---" + startTouchI.x + "  " + startTouchII.x + "  ---second-->  " + finalPos.x;
                } else {
                    zoomout();
                }
            }
            thecoordsText.value = startPos.x + "," + startPos.y + "," + finalPos.x + "," + finalPos.y + "," + 1;
            button.click("1");
        }
        touchevent = false;
    }
    var timer;
    selectionCanvasElement.on('touchmove', function (ev) {
        if (mouseevent)
            return;
        touchevent = true;
        if (ev.originalEvent.touches.length !== 2)
            return;
        finalTouchI = {x: parseInt(ev.originalEvent.touches[0].pageX - canvasOffset.left, 10), y: parseInt(ev.originalEvent.touches[0].pageY - canvasOffset.top, 10)};
        finalTouchII = {x: parseInt(ev.originalEvent.touches[1].pageX - canvasOffset.left, 10), y: parseInt(ev.originalEvent.touches[1].pageY - canvasOffset.top, 10)};
        rect(ctx);
        clearTimeout(timer);
        timer = setTimeout(touchEnd, 300);
    });
};