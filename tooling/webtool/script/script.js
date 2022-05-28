//transform: rotate(90deg);
jQuery(document).ready(function($) {
    window.speed = 300;
    //$("#boat").animate({ top: y, left: x }, 1200);
    init()
});


class Boat {
    constructor(x, y, orientation) {
        let boatHTML = document.getElementById("boat");
        this.x = x;
        this.y = y;
        this.orientation = orientation;
        this.getX = function() { return translateX(this.x); };
        this.getY = function() { return translateY(this.y); };
        window.sea = document.getElementById('sea');
    }
}

class Checkpoint {
    constructor(x, y, radius) {
        this.position = new Position(x, y, 0)
        this.radius = radius;
    }
}


class Beacon {
    constructor(x, y) {
        this.position = new Position(x, y, 0)
        if (this.position.x > window.maxX) window.maxX = this.position.x;
        if (this.position.y > window.maxY) window.maxY = this.position.y;
        this.radius = 100;
    }
}
class Position {
    constructor(x, y, orientation) {
        this.x = (x * 0.5) + window.sea.offsetWidth / 2;
        this.y = (y * 0.5) + window.sea.offsetHeight / 2;
        this.orientation = orientation;
    }
}

class Reef_Circle {
    constructor(radius, x, y, orientation) {
        this.type = "Reef_Circle"
        this.position = new Position(x, y, orientation);
        this.circle = new Circle(radius);
    }
}

class Reef_Rectangle {
    constructor(width, height, x, y, orientation) {
        this.type = "Reef_Rectangle"
        this.position = new Position(x, y, orientation);
        this.rectangle = new Rectangle(width, height);
    }
}

class Reef_Polygone {
    constructor(x, y, orientation, listCorner) {
        this.type = "Reef_Polygone"
        this.position = new Position(x, y, orientation);
        this.polygone = new Polygone(listCorner, this.position);
    }
}
class Stream {
    constructor(width, height, strength, x, y, orientation) {
        this.type = "Stream"
        this.position = new Position(x, y, orientation);
        this.rectangle = new Rectangle(width, height);
        this.strength = strength;
    }
}

class Rectangle {
    constructor(width, height) {
        this.width = width * 0.5;
        this.height = height * 0.5;
    }
}

class Circle {
    constructor(radius) {
        this.radius = radius;
    }
}

class Polygone {
    constructor(listCorner, center) {
        this.listPosition = this.generateListPosition(listCorner, center);
        this.width = this.findWidth();
        this.height = this.findHeight();
    }
    getListPosition() {
        let stringListPosition = this.listPosition[0].x + "," + this.listPosition[0].y;
        for (let i = 1; i < this.listPosition.length; i++) {
            let currentPosition = this.listPosition[i];
            stringListPosition += " " + currentPosition.x + "," + currentPosition.y;
        }
        return stringListPosition;
    }

    generateListPosition(listCorner, center) {
        //100/200 45/100 41/785
        let finalListPosition = [];
        let stringListPosition = listCorner.split(' ');
        for (let i = 0; i < stringListPosition.length; i++) {
            let stringPosition = stringListPosition[i].split('/');
            finalListPosition.push(new Position(stringPosition[0], stringPosition[1], 0));
        }
        return this.convertListPositionForSvg(finalListPosition, center);
    }

    findHeight() {
        //100/200 45/100 41/785
        //Y
        let min = this.listPosition[0].y;
        let max = this.listPosition[0].y;
        for (let i = 1; i < this.listPosition.length; i++) {
            if (min > this.listPosition[i].y) min = this.listPosition[i].y;
            if (max < this.listPosition[i].y) max = this.listPosition[i].y;
        }
        return Math.abs(max - min);
    }

    findWidth() {
        //100/200 45/100 41/785
        //X
        let min = this.listPosition[0].x;
        let max = this.listPosition[0].x;
        for (let i = 1; i < this.listPosition.length; i++) {
            if (min > this.listPosition[i].x) min = this.listPosition[i].x;
            if (max < this.listPosition[i].x) max = this.listPosition[i].x;
        }
        return Math.abs(max - min);
    }

    convertListPositionForSvg(listPosition, center) {
        for (let i = 0; i < listPosition.length; i++) {
            listPosition[i].x -= center.x;
            listPosition[i].y -= center.y;
        }
        return listPosition;
    }
}

function init() {
    window.checkpoints = [];
    window.seaEntities = [];
    window.beacons = [];
    window.usedBeacons = [];
    window.maxX = 1000;
    window.maxY = 1000;


    window.cameraLock = false;
    addListener();
    listenerToDownloadReduceSea();
    window.boat = new Boat(0, 0, 0);
    window.saveMyLife = document.getElementById("saveMyLife");
    window.lastX = translateX(0);
    window.lastY = translateY(0);
    document.getElementById('boat').style.transform = "rotate(-90deg)";
    $("#boat").animate({ top: window.lastY, left: window.lastX }, 1200, function() {
        scrollToTheBoat(window.lastX / 2, window.lastY / 2);
    });
    getInputJson();
}



function scrollToTheBoat(x, y) {
    window.scroll(x + x / 2, y + y / 2);
}

function move(input) {
    placeBoatAtStart(input, 0);
    for (let i = 1; i < input.length; i++) {
        storeCurrentPosition();
        updateBoatPosition(input, i);
        drawpath(window.boat.getX(), window.boat.getY());
        drawBoat();
        if (window.cameraLock)
            scrollToTheBoat(window.lastX / 2, window.lastY / 2);
    }
}

function placeBoatAtStart(input) {
    updateBoatPosition(input, 0);
    drawBoat();
}


function storeCurrentPosition() {
    window.lastX = window.boat.getX();
    window.lastY = window.boat.getY();
}

function updateBoatPosition(input, i) {
    let currentPos = input[i].split(';');
    window.boat.x = currentPos[0];
    window.boat.y = currentPos[1];
    window.boat.orientation = currentPos[2];
}

function drawBoat() {
    let newX = window.boat.getX();
    let newY = window.boat.getY();
    let orientation = window.boat.orientation;
    let rotation = (orientation * 180) / Math.PI;
    $('#boat').animate({ borderSpacing: orientation }, {
        step: function(now, fx) {
            console.log("orientation=" + orientation);
            $(this).css({
                '-webkit-transform': 'rotate(' + rotation + 'deg)',
                '-moz-transform': 'rotate(' + rotation + 'deg)',
                '-o-transform': 'rotate(' + rotation + 'deg)',
                '-ms-transform': 'rotate(' + rotation + 'deg)',
                'transform': 'rotate(' + rotation + 'deg)'
            });
        }
    }, 'linear');
    $("#boat").animate({ top: newY - (window.saveMyLife.height / 2), left: newX - (window.saveMyLife.width / 2) }, window.speed);
}

function translateX(x) {
    return (x * 0.5) + (window.sea.offsetWidth / 2);
}

function translateY(y) {
    return (y * 0.5) + (window.sea.offsetHeight / 2);
}

function createCheckpoints(input) {
    let checkpointList = document.getElementById('sea');
    for (let i = 0; i < input.length; i++) {
        let parameters = input[i].split(';');
        window.checkpoints.push(new Checkpoint(parameters[0], parameters[1], parameters[2]));
        let check = "<div id='" + i + "' class='checkpoint'></div>"
        checkpointList.innerHTML += check;
    }
}

function createSeaEntities(input) {
    let seaEntitiesList = document.getElementById('sea');

    for (let i = 0; i < input.length; i++) {
        let polygoneWrapper = document.getElementById('polygone');
        let isPolygone = false;
        let seaEntitie;
        let parameters = input[i].split(';');
        if (parameters[0] == "reef") {
            if (parameters[1] == "rect") {
                window.seaEntities.push(new Reef_Rectangle(parameters[2], parameters[3], parameters[4], parameters[5], parameters[6]));
                seaEntitie = "<div id='seaEnt_" + i + "' class='seaEntitie Reef'> 5 </div>"
            } else if (parameters[1] == "circle") {
                window.seaEntities.push(new Reef_Circle(parameters[2], parameters[3], parameters[4], parameters[5]));
                seaEntitie = "<div id='seaEnt_" + i + "' class='seaEntitie Reef Reef_Circle'></div>"
            } else if (parameters[1] == "poly") {
                const polygoneReef = new Reef_Polygone(parameters[2], parameters[3], parameters[4], parameters[5])
                window.seaEntities.push(polygoneReef);
                //seaEntitie = "<div class='seaEntitie Reef_Polygone' id='seaEnt_" + i + "'><svg class='svg' id='svgSeaEnt_" + i + "'><polygon class='Reef poly' id='polySeaEnt_" + i + "' points='" + polygoneReef.polygone.getListPosition() + "'></svg></div>";
                //let top = polygoneReef.position.y - (polygoneReef.polygone.height / 2);
                //let left = polygoneReef.position.x - (polygoneReef.polygone.width / 2);
                seaEntitie = "<div x=" + polygoneReef.position.x + " y=" + polygoneReef.position.y + " style='height:" + polygoneReef.polygone.height + "px; width:" + polygoneReef.polygone.width + "px; ' class='seaEntitie Reef_Polygone' id='seaEnt_" + i + "'><svg class='svg' id='svgSeaEnt_" + i + "' style='height:" + polygoneReef.polygone.height + "px; width:" + polygoneReef.polygone.width + "px'><polygon class='Reef polygon' id='polySeaEnt_" + i + "' points='" + polygoneReef.polygone.getListPosition() + "' transform='translate(" + polygoneReef.polygone.width / 3 + " " + polygoneReef.polygone.height / 2 + ")' style='fill:rgb(39, 39, 39);'/></svg></div>"
            }
        } else if (parameters[0] == "stream") {
            window.seaEntities.push(new Stream(parameters[1], parameters[2], parameters[3], parameters[4], parameters[5], parameters[6]));
            seaEntitie = "<div id='seaEnt_" + i + "' class='seaEntitie Stream'> 5 </div>"
        }
        seaEntitiesList.innerHTML += seaEntitie;
    }
}

function createBeacon(input) {
    let sea = document.getElementById('sea');
    for (let i = 0; i < input.length; i++) {
        let parameters = input[i].split(';');
        window.beacons.push(new Beacon(parameters[0], parameters[1]));
        let check = "<div id='beacon_" + i + "' class='beacon'></div>"
        sea.innerHTML += check;
    }
}

function createUsedBeacon(input) {
    let sea = document.getElementById('sea');
    for (let i = 0; i < input.length; i++) {
        let parameters = input[i].split(';');
        window.usedBeacons.push(new Beacon(parameters[0], parameters[1]));
        let check = "<div id='Ubeacon_" + i + "' class='beacon used'></div>"
        sea.innerHTML += check;
    }
}

function animateCheckpoints() {
    $('.checkpoint').each(function() {
        let id = $(this).attr("id");
        let checkpoint = window.checkpoints[id];
        let radius = checkpoint.radius;
        $(this).css({ top: checkpoint.position.y - (radius / 2), left: checkpoint.position.x - (radius / 2) });
        $(this).css({ height: radius, width: radius });
    });
}

function animateBeacon() {
    $('.beacon').each(function() {
        let id = $(this).attr("id").slice(7);
        let beacon = window.beacons[id];
        let radius = beacon.radius;
        $(this).css({ top: beacon.position.y - (radius / 2), left: beacon.position.x - (radius / 2) });
        $(this).css({ height: radius, width: radius });
    });
}

function animateUsedBeacon() {
    $('.used').each(function() {
        let id = $(this).attr("id").slice(8);
        let beacon = window.usedBeacons[id];
        let radius = beacon.radius;
        $(this).css({ top: beacon.position.y - (radius / 2), left: beacon.position.x - (radius / 2) });
        $(this).css({ height: radius, width: radius });
    });
}

function animateSeaEntities() {
    $('.seaEntitie').each(function() {
        let id = $(this).attr("id").slice(7);
        let seaEntite = window.seaEntities[id];
        if (seaEntite.type == "Reef_Rectangle") {
            $(this).css('transform', 'rotate(' + seaEntite.position.orientation + 'rad)');
            $(this).css({ top: seaEntite.position.y - (seaEntite.rectangle.height / 2), left: seaEntite.position.x - (seaEntite.rectangle.width / 2) }, 1000);
            $(this).css({ height: seaEntite.rectangle.height, width: seaEntite.rectangle.width }, 1000);
        } else if (seaEntite.type == "Reef_Polygone") {
            //$(this).css({ top: seaEntite.position.y - (seaEntite.polygone.height / 2), left: seaEntite.position.x - (seaEntite.polygone.width / 3) }, 1000);
            $(this).css({
                top: seaEntite.position.y - ((seaEntite.polygone.height / 2) + ((seaEntite.polygone.height / 2) - (seaEntite.polygone.height / 3))),
                left: seaEntite.position.x - ((seaEntite.polygone.width / 2.6)) //- ((seaEntite.polygone.width / 2) - (seaEntite.polygone.width / 3))
            }, 1000);
            $(this).css('transform', 'rotate(' + seaEntite.position.orientation + 'rad)');

            //$(this).css({ height: seaEntite.polygone.height, width: seaEntite.polygone.width }, 1000);

            /*let newX = seaEntite.position.x + seaEntite.polygone.width / 2;
            let newY = seaEntite.position.y + seaEntite.polygone.height / 2;
            $(this).attr('transform', 'rotate(' + radians_to_degrees(seaEntite.position.orientation) + ' ' + newX + ' ' + newY + ')');
            $(this).attr('transform', 'translate(' + seaEntite.position.x + ',' + seaEntite.position.y + ')');

            $(this).attr('x', seaEntite.position.x);
            $(this).attr('y', seaEntite.position.y);
            $(this).attr('width', seaEntite.polygone.width);
            $(this).attr('height', seaEntite.polygone.height);

            $(this).css('transform', 'translate(' + seaEntite.position.x + ',' + seaEntite.position.y + ')');*/
            //$(this).css('transform', 'rotate(' + radians_to_degrees(seaEntite.position.orientation) + ',' + seaEntite.position.x + ',' + seaEntite.position.y + ')');
            //$(this).css({ "transform-origin": seaEntite.position.x + " " + seaEntite.position.y }, 1000);
            //$(this).css('transform', 'rotate(' + radians_to_degrees(seaEntite.position.orientation) + ' ' + seaEntite.position.x + seaEntite.polygone.width / 2 + ' ' + seaEntite.position.y + seaEntite.polygone.height / 2 + ')');
            //;
            //$(this).css({ top: seaEntite.position.y - (seaEntite.polygone.height / 2), left: seaEntite.position.x - (seaEntite.polygone.width / 2) }, 1000);
            //$(this).css({ height: seaEntite.polygone.height, width: seaEntite.polygone.width }, 1000);
        } else if (seaEntite.type == "Reef_Circle") {
            $(this).css({ top: seaEntite.position.y - (seaEntite.circle.radius / 2), left: seaEntite.position.x - (seaEntite.circle.radius / 2) }, 1000);
            $(this).css({ height: seaEntite.circle.radius, width: seaEntite.circle.radius }, 1000);
        } else if (seaEntite.type == "Stream") {
            $(this).css('transform', 'rotate(' + seaEntite.position.orientation + 'rad)');
            $(this).css({ top: seaEntite.position.y - (seaEntite.rectangle.height / 2), left: seaEntite.position.x - (seaEntite.rectangle.width / 2) }, 1000);
            $(this).css({ height: seaEntite.rectangle.height, width: seaEntite.rectangle.width }, 1000);
        }
    });
}


function setUsedBeacon(input) {
    console.log("UsedBeacon:\n" + input)
    let sea = document.getElementById('sea');
    for (let i = 0; i < input.length; i++) {
        let parameters = input[i].split(';');
        let x = parseFloat(parameters[0]);
        let y = parseFloat(parameters[1]);
        let idUsed = findBeaconId(new Position(x, y, 0));
        let beaconUsed = document.getElementById("beacon_" + idUsed);
        beaconUsed.classList.add("used");
    }
}

function findBeaconId(positionUsed) {
    for (let i = 0; i < window.beacons.length; i++) {
        if (i == 245)
            console.log(window.beacons[i]);
        if (window.beacons[i].position.x == positionUsed.x && window.beacons[i].position.y == positionUsed.y) {
            return i;
        }
    }
}

function getText(textarea) {
    array = textarea.value.replace(/\s+/g, ' ').split(' ').filter((e) => e.length > 0);
    return array
}

function drawpath(newX, newY) {
    let pathX = window.lastX;
    let pathY = window.lastY;
    let draw = document.getElementById("draw");

    let path = "<path class='absolute' d='M " + pathX + " " + pathY + " L " + Math.round(newX) + " " + Math.round(newY) + "' stroke='red' stroke-width='3' fill='none' />";
    let point = "<circle class='absolute' id='pointA' cx='" + pathX + "' cy='" + pathY + "' r='3' />";
    let point2 = "<circle class='absolute' id='pointA' cx='" + newX + "' cy='" + newY + "' r='3' />";

    draw.innerHTML += path;
    draw.innerHTML += point;
    draw.innerHTML += point2;
}


function startRun(jsonIn) {
    if (!document.getElementById("cameraLock").checked) window.cameraLock = false;
    let input;
    let splitChar;
    let inputArray;
    if (jsonIn != undefined) {
        input = jsonIn;
        splitChar = "|";
        inputArray = input.split("---|");
    } else {
        input = document.querySelector('#log').value;
        splitChar = "|\n";
        inputArray = input.split("---|\n");
    }
    console.log(input);
    let checkpoints = removeEmpty(inputArray[0].split(splitChar));
    let seaEntities = removeEmpty(inputArray[1].split(splitChar));
    let beacons = removeEmpty(inputArray[2].split(splitChar));
    let beaconsUsed = removeEmpty(inputArray[3].split(splitChar));
    let coord = removeEmpty(inputArray[4].split(splitChar));

    /*console.log("checkpoints:\n" + checkpoints);
    console.log("seaEntities:\n" + seaEntities);
    console.log("coord:\n" + coord);*/

    console.log("---compute score---");
    score.innerText = "Number of round: " + countRound(coord);
    console.log("---create checkpoint---");
    createCheckpoints(checkpoints);
    console.log("---animate checkpoint---");
    animateCheckpoints();
    console.log("---create SeaEntities---");
    createSeaEntities(seaEntities);
    console.log("---animate SeaEntities---");
    animateSeaEntities();
    console.log("---create Beacon---");
    createBeacon(beacons);
    console.log("---animate Beacon---");
    animateBeacon();
    console.log("---create usedBeacon---");
    createUsedBeacon(beaconsUsed);
    console.log("---animate usedBeacon---");
    animateUsedBeacon();
    console.log("---set the dimension of the sea---");
    setSeaDimension();
    /*console.log("---create setUsedBeacon---");
    setUsedBeacon(beaconsUsed);*/
    console.log("---move boat---");
    move(coord);
    $('#sea').css({ "min-height": window.maxY + 1000, "min-width": window.maxX + 1000 });
    sendDataToBack();
}

function addListener() {
    document.querySelector('#start').addEventListener('click', function() {
        startRun();
    });
    document.querySelector('#reset').addEventListener('click', function(event) {
        location.reload();
    });
}

function countRound(input) {
    return input.length;
}

function removeEmpty(list) {
    let array = [];
    for (let i = 0; i < list.length; i++) {
        if (list[i] != "") array.push(list[i]);
    }
    return array;
}

function getCalcPosition(x, y, radius) {
    let vectorX = /*translateX(0)*/ -parseInt(x);
    let vectorY = /*translateY(0)*/ -parseInt(y);
    let norm = Math.sqrt(vectorX * vectorX + vectorY * vectorY);

    let unitX = vectorX / norm;
    let unitY = vectorY / norm;
    let newX = parseInt(x) + (radius * 0.5) * unitX;
    let newY = parseInt(y) + (radius * 0.5) * unitY;
    let fictiousPosition = new Position(newX, newY, 0);
    return fictiousPosition;
}

function setSeaDimension() {
    //Récuperer la balise la plus basse (avec le y le plus grand)
    //Récuperer la balise la plus à droite (avec le x le plus grand)
    $('body').css({ "min-height": window.maxY + 1000, "min-width": window.maxX + 1000 });

}

function listenerToDownloadReduceSea() {
    document.querySelector('#download').addEventListener('click', function(event) {
        downloadimage();
    });
}

function getURL() {
    return window.location.href;
}

function getInputJson() {
    let url = getURL();
    let json = url.split("input=")[1];
    console.log(json)
    if (json != undefined) {
        startRun(json);
        sendDataToBack();
    }
}

function downloadimage() {
    $('#sea').css({ "min-height": window.maxY + 1000, "min-width": window.maxX + 1000 });
    var sea = document.getElementById('sea'); // full page 
    elementToLinkImage(sea);
    sendDataToBack();
    //sendImageToImageBB();
}

function elementToLinkImage(element) {
    html2canvas(element, { allowTaint: true, foreignObjectRendering: true }).then(function(canvas) {
        var link = document.createElement("a");
        document.body.appendChild(link);
        link.download = "html_image.png";
        var imgData = canvas.toDataURL('image/jpeg', 0.1);
        //var imgData = canvas.toDataURL('image/png');
        link.href = imgData;
        link.target = '_blank';
        link.click();
        return imgData;
    });
}
function resizeBase64Img(base64, newWidth, newHeight) {
    return new Promise((resolve, reject) => {
        var canvas = document.createElement("canvas");
        canvas.style.width = newWidth.toString() + "px";
        canvas.style.height = newHeight.toString() + "px";
        let context = canvas.getContext("2d");
        let img = document.createElement("img");
        img.src = base64;
        img.onload = function() {
            context.scale(newWidth / img.width, newHeight / img.height);
            context.drawImage(img, 0, 0);
            resolve(canvas.toDataURL());
        }
    });
}

function sendDataToBack() {
    $('#sea').css({"min-height": window.maxY + 1000, "min-width": window.maxX + 1000});
    var container = document.getElementById('sea'); // full page
    html2canvas(container, {allowTaint: true, foreignObjectRendering: true}).then(function (canvas) {
        var link = document.createElement("a");
        document.body.appendChild(link);
        link.download = "html_image.png";
        var imgData = canvas.toDataURL('image/jpeg', 0.1);
        var form_data = new FormData();
        form_data.append('file', imgData);
        $.ajax({
            url: '../backend/decode64.php', // <-- point to server-side PHP script
            dataType: 'text', // <-- what to expect back from the PHP script, if anything
            cache: false,
            contentType: false,
            processData: false,
            data: form_data,
            type: 'post',
            success: function(php_script_response) {
                console.log(php_script_response); // <-- display response from the PHP script, if any
            }
        });
    });
}