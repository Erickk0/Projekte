	
/*****
/*
/* Beispielprogramm für die Lehrveranstaltung Computergraphik
/* HS RheinMain
/* Prof. Dr. Ralf Dörner
/*
/* basierend auf einem Programm von Edward Angel
/* http://www.cs.unm.edu/~angel/WebGL/
/*
/****/


/***   Deklaration globaler Variablen */

// Referenz auf Bereich, in den gezeichnet wird
var canvas;

// Referenz auf WebGL-Kontext, über die OpenGL Befehle ausgeführt werden
var gl;

// Referenz auf die Shaderprogramme
var program;

// Matrix für die Umrechnung Objektkoordinaten -> Weltkoordinaten
var model;

// Matrix für die Umrechnung Weltkoordinaten -> Kamerakoordinaten
var view; 

// Matrix für die Umrechnung Kamerakoordinaten -> Clippingkoordinaten
var projection;

// Matrix für die Umrechnung von Normalen aus Objektkoordinaten -> Viewkoordinaten
var normalMat;

// Flag, das angibt, ob eine Beleuchtungsrechnung durchgeführt wird (true)
// oder ob einfach die übergebenen Eckpunktfarben übernommen werden (false)
var lighting = true;

// Anzahl der Eckpunkte der zu zeichenden Objekte 
var numVertices  = 0;

// Array, in dem die Koordinaten der Eckpunkte der zu zeichnenden Objekte eingetragen werden
var vertices = [];

// Array, in dem die Farben der Eckpunkte der zu zeichnenden Objekte eingetragen werden
var colors = [];

// Array, in dem die Eckpunktkoordinaten der zu zeichnenden Objekte eingetragen werden
var pointsArray = [];

// Array, in dem die Normale je Eckpunkt der zu zeichnenden Objekte eingetragen werden
var normalsArray = [];

// Array, in dem die Farbwerte je Eckpunkt der zu zeichnenden Objekte eingetragen werden
var colorsArray = [];

// Variablen für die Drehung des Würfels
var axis = 0;
var theta =[0, 0, 0];

// Variablen für die Eigenrotation
selfAxis=0;
var isOn = false;

// Rotation Teapot
var tearotation= 0;

// Variablen, um die Anzahl der Frames pro Sekunde zu ermitteln
var then = Date.now() / 1000;
var counter = 0;

// Textur
var texture;
var image = new Image();

var imageArray = new Float32Array([ /* 1.Seite */			0, 0, 0, 2, 2, 2, 0, 0, 2, 2, 2, 0,
									/* 2.Seite */			0, 0, 0, 2, 2, 2, 0, 0, 2, 2, 2, 0,
									/* 3.Seite */			0, 0, 0, 2, 2, 2, 0, 0, 2, 2, 2, 0,
									/* 4.Seite */			0, 0, 0, 2, 2, 2, 0, 0, 2, 2, 2, 0,
									/* 5.Seite */			0, 0, 0, 2, 2, 2, 0, 0, 2, 2, 2, 0,
									/* 6.Seite */			0, 0, 0, 2, 2, 2, 0, 0, 2, 2, 2, 0]);


var materialAmbient = vec4(0.2, 0.2, 0.2, 1.0);
var materialSpecular = vec4(1.0, 1.0, 1.0, 1.0);

var teapotNormalData = [];
var teapotVertexData = [];
var teapotIndexData = [];
var teapotVertexIndexBuffer;

var request = new XMLHttpRequest();
request.onreadystatechange = handleResponse;


function handleResponse(){
	if (request.readyState == 4 && request.status == 200) {
		var teapotData = JSON.parse(request.responseText);
        var i;
        for (i=0; i< teapotData.vertexNormals.length; i++) {
            teapotNormalData.push(teapotData.vertexNormals[i]);
			teapotVertexData.push(teapotData.vertexPositions[i]);
			teapotIndexData.push(teapotData.indices[i]);
        }
        for (i=teapotData.vertexNormals.length; i< teapotData.indices.length; i++) {
             teapotIndexData.push(teapotData.indices[i]);
        }
    }
}



function drawTeapot() {

    var teapotVertexNormalBuffer = gl.createBuffer();
    gl.bindBuffer(gl.ARRAY_BUFFER, teapotVertexNormalBuffer);
    gl.bufferData(gl.ARRAY_BUFFER, new Float32Array(teapotNormalData), gl.STATIC_DRAW);
    teapotVertexNormalBuffer.itemSize = 3;
    teapotVertexNormalBuffer.numItems = teapotNormalData.length / 3;

    var teapotVertexPositionBuffer = gl.createBuffer();
    gl.bindBuffer(gl.ARRAY_BUFFER, teapotVertexPositionBuffer);
    gl.bufferData(gl.ARRAY_BUFFER, new Float32Array(teapotVertexData), gl.STATIC_DRAW);
    teapotVertexPositionBuffer.itemSize = 3;
    teapotVertexPositionBuffer.numItems = teapotVertexData.length / 3;

    teapotVertexIndexBuffer = gl.createBuffer();
    gl.bindBuffer(gl.ELEMENT_ARRAY_BUFFER, teapotVertexIndexBuffer);
    gl.bufferData(gl.ELEMENT_ARRAY_BUFFER, new Uint16Array(teapotIndexData), gl.STATIC_DRAW);
    teapotVertexIndexBuffer.itemSize = 1;
    teapotVertexIndexBuffer.numItems = teapotIndexData.length;

    gl.enableVertexAttribArray(gl.getAttribLocation(program, "vPosition"));
    gl.enableVertexAttribArray(gl.getAttribLocation(program, "vNormal"));


    gl.bindBuffer(gl.ARRAY_BUFFER, teapotVertexPositionBuffer);
    gl.vertexAttribPointer(gl.getAttribLocation(program, "vPosition"), teapotVertexPositionBuffer.itemSize, gl.FLOAT, false, 0, 0);

    gl.bindBuffer(gl.ARRAY_BUFFER, teapotVertexNormalBuffer);
    gl.vertexAttribPointer(gl.getAttribLocation(program, "vNormal"), teapotVertexNormalBuffer.itemSize, gl.FLOAT, false, 0, 0);

    gl.bindBuffer(gl.ELEMENT_ARRAY_BUFFER, teapotVertexIndexBuffer);

    gl.disableVertexAttribArray(gl.getAttribLocation(program, "vColor"));
    gl.disableVertexAttribArray(gl.getAttribLocation(program, "vTexCoord"));

}



//
// Funktion, die ein Quadrat in das pointsArray, colorsArray und normalsArray einträgt
// Das Quadrat wird dabei in zwei Dreiecke trianguliert, da OpenGL keine Vierecke 
// nativ zeichnen kann.
//
// Übergeben werden für Indices auf die vier Eckpunkte des Vierecks
//

function quad(a, b, c, d) {

     // zunächst wird die Normale des Vierecks berechnet. t1 ist der Vektor von Eckpunkt a zu Eckpunkt b
     // t2 ist der Vektor von Eckpunkt a zu Eckpunkt c. Die Normale ist dann das 
     // Kreuzprodukt von t1 und t2
     var t1 = subtract(vertices[b], vertices[a]);
     var t2 = subtract(vertices[c], vertices[a]);
     var normal = cross(t1, t2);
     normal = vec3(normal);

     // und hier kommt die Eintragung der Infos für jeden Eckpunkt (Koordinaten, Normale, Farbe) in die globalen Arrays
     // allen Eckpunkten wird die gleiche Farbe zugeordnet, dabei 
    
     // erstes Dreieck
     pointsArray.push(vertices[a]); 
     normalsArray.push(normal);
	   colorsArray.push(colors[a]);
    
     pointsArray.push(vertices[b]); 
     normalsArray.push(normal);
	   colorsArray.push(colors[a]);
    
     pointsArray.push(vertices[c]); 
     normalsArray.push(normal);
	   colorsArray.push(colors[a]);
     
     // zweites Dreieck
     pointsArray.push(vertices[a]);  
     normalsArray.push(normal); 
	   colorsArray.push(colors[a]);
     
     pointsArray.push(vertices[c]); 
     normalsArray.push(normal); 
	   colorsArray.push(colors[a]);
     
     pointsArray.push(vertices[d]); 
     normalsArray.push(normal);
	   colorsArray.push(colors[a]);
	
     // durch die beiden Dreiecke wurden 6 Eckpunkte in die Array eingetragen
     numVertices += 6;    
}

// Zeichnet ein Dreieck abgeleiet von quad()
function triangle(a,b,c){
	 
	 var t1 = subtract(vertices[b], vertices[a]);
     var t2 = subtract(vertices[c], vertices[a]);
     var normal = cross(t1, t2);
     normal = vec3(normal);
	 
	 // Dreieck erstellen
     pointsArray.push(vertices[a]); 
     normalsArray.push(normal);
	   colorsArray.push(colors[a]);
    
     pointsArray.push(vertices[b]); 
     normalsArray.push(normal);
	   colorsArray.push(colors[a]);
    
     pointsArray.push(vertices[c]); 
     normalsArray.push(normal);
	   colorsArray.push(colors[a]);
	   
	   numVertices+=3;
}

//
//	Funktion, die eine Pyramide zeichnet 
//	-- abgeleitet von drawCube()

function drawPyramid(){
	
	// Punkte der Grundfläche und der Spitze
	vertices = [
		vec4(-2, 0, 1, 1), 
        vec4(-2, 0, -1, 1), 
        vec4(2, 0, -1, 1), 
        vec4(2, 0, 1, 1), 
        vec4(0, 4, 0, 1) 
    ];
	
	colors = [
		vec4(1.0, 0.0, 0.0, 1.0), 
		vec4(1.0, 1.0, 0.0, 1.0),
		vec4(0.0, 1.0, 0.0, 1.0),
		vec4(0.0, 1.0, 1.0, 1.0),
		vec4(0.0, 0.0, 1.0, 1.0),
    ];
	
	quad(0,1,2,3);
	triangle(0,3,4);
	triangle(0,4,1);
	triangle(2,1,4);
	triangle(2,4,3);
	
	    var nBuffer = gl.createBuffer();
    gl.bindBuffer( gl.ARRAY_BUFFER, nBuffer );
    gl.bufferData( gl.ARRAY_BUFFER, flatten(normalsArray), gl.STATIC_DRAW );
    
    var vNormal = gl.getAttribLocation( program, "vNormal" );
	  gl.vertexAttribPointer( vNormal, 3, gl.FLOAT, false, 0, 0 );
    gl.enableVertexAttribArray( vNormal );

    var vBuffer = gl.createBuffer();
    gl.bindBuffer( gl.ARRAY_BUFFER, vBuffer );
    gl.bufferData( gl.ARRAY_BUFFER, flatten(pointsArray), gl.STATIC_DRAW );
    
    var vPosition = gl.getAttribLocation(program, "vPosition");
    gl.vertexAttribPointer(vPosition, 4, gl.FLOAT, false, 0, 0);
    gl.enableVertexAttribArray(vPosition);
    
    var cBuffer = gl.createBuffer();
    gl.bindBuffer( gl.ARRAY_BUFFER, cBuffer );
    gl.bufferData( gl.ARRAY_BUFFER, flatten(colorsArray), gl.STATIC_DRAW );
    
    var cPosition = gl.getAttribLocation(program, "vColor");
    gl.vertexAttribPointer(cPosition, 4, gl.FLOAT, false, 0, 0);
    gl.enableVertexAttribArray(cPosition);
	
}


//
// Funktion, die einen Würfel zeichnet (Mittelpunkt liegt im Ursprung, Kantenlänge beträgt 1)
//

function drawCube()
{

    // zunächst werden die Koordinaten der 8 Eckpunkte des Würfels definiert
    vertices = [
        vec4( -0.5, -0.5,  0.5, 1.0 ), // 0
        vec4( -0.5,  0.5,  0.5, 1.0 ), // 1
        vec4( 0.5,  0.5,  0.5, 1.0 ),  // 2 
        vec4( 0.5, -0.5,  0.5, 1.0 ),  // 3
        vec4( -0.5, -0.5, -0.5, 1.0 ), // 4
        vec4( -0.5,  0.5, -0.5, 1.0 ), // 5
        vec4( 0.5,  0.5, -0.5, 1.0 ),  // 6
        vec4( 0.5, -0.5, -0.5, 1.0 )   // 7
    ];

    // hier werden verschiedene Farben definiert (je eine pro Eckpunkt)
    colors = [
        vec4(1.0, 0.0, 0.0, 1.0), 
	      vec4(1.0, 0.0, 0.0, 1.0),
        vec4(1.0, 0.0, 0.0, 1.0),
        vec4(0.0, 0.0, 0.0, 1.0),
	      vec4(1.0, 0.0, 0.0, 1.0),
	      vec4(1.0, 0.0, 0.0, 1.0),
        vec4(0.0, 0.0, 0.0, 1.0),
	      vec4(1.0, 0.0, 0.0, 1.0)
    ];

    // und hier werden die Daten der 6 Seiten des Würfels in die globalen Arrays eingetragen
    // jede Würfelseite erhält eine andere Farbe
    quad( 1, 0, 3, 2 );
    quad( 2, 3, 7, 6 );
    quad( 3, 0, 4, 7 );
    quad( 6, 5, 1, 2 );
    quad( 4, 5, 6, 7 );
    quad( 5, 4, 0, 1 );
    
    
    // die eingetragenen Werte werden an den Shader übergeben
    
    var nBuffer = gl.createBuffer();
    gl.bindBuffer( gl.ARRAY_BUFFER, nBuffer );
    gl.bufferData( gl.ARRAY_BUFFER, flatten(normalsArray), gl.STATIC_DRAW );
    
    var vNormal = gl.getAttribLocation( program, "vNormal" );
	  gl.vertexAttribPointer( vNormal, 3, gl.FLOAT, false, 0, 0 );
    gl.enableVertexAttribArray( vNormal );

    var vBuffer = gl.createBuffer();
    gl.bindBuffer( gl.ARRAY_BUFFER, vBuffer );
    gl.bufferData( gl.ARRAY_BUFFER, flatten(pointsArray), gl.STATIC_DRAW );
    
    var vPosition = gl.getAttribLocation(program, "vPosition");
    gl.vertexAttribPointer(vPosition, 4, gl.FLOAT, false, 0, 0);
    gl.enableVertexAttribArray(vPosition);
    
    var cBuffer = gl.createBuffer();
    gl.bindBuffer( gl.ARRAY_BUFFER, cBuffer );
    gl.bufferData( gl.ARRAY_BUFFER, flatten(colorsArray), gl.STATIC_DRAW );
    
    var cPosition = gl.getAttribLocation(program, "vColor");
    gl.vertexAttribPointer(cPosition, 4, gl.FLOAT, false, 0, 0);
    gl.enableVertexAttribArray(cPosition);
	
	 //        var Tx = 50.5, Ty = 50.5, Tz = 0.0;
     //    var translation = gl.getUniformLocation(program, 'translation');
     //    gl.uniform4f(translation, Tx, Ty, Tz, 0.0);
    
}


/*** Funktionen zum Aufbau der Szene */

//
// Funktion zum setzen der inneren und äußeren Parameter der Kamera
//

function setCamera()
{
  
    // es wird ermittelt, welches Element aus der Kameraauswahlliste aktiv ist
    var camIndex = document.getElementById("Cameralist").selectedIndex;

    // Punkt, an dem die Kamera steht  // Punkt, auf den die Kamera schaut  // Vektor, der nach oben zeigt  
	  var eye,vrp,upv;
	
    if (camIndex == 0){
        // hier wird die erste Kameraposition definiert
		    eye = vec3(12.0,12.0,4.0);
     		vrp = vec3(0.0,0.0,0.0);
     		upv = vec3(0.0,1.0,0.0);
	  };
	  
	      if (camIndex == 1){
        // hier wird die zweite Kameraposition definiert
		    eye = vec3(10.0,0.0,0.0);
     		vrp = vec3(-1.0,0.0,0.0);
     		upv = vec3(0.0,1.0,0.0);
	  };
	  
	      if (camIndex == 2){
        // hier wird die dritte Kameraposition definiert
		    eye = vec3(0.0,10.0,0.0);
     		vrp = vec3(0.0,-1.0,0.0);
     		upv = vec3(-1.0,0.0,0.0);
	  };
	  
	      if (camIndex == 3){
        // hier wird die vierte Kameraposition definiert
		    eye = vec3(0.0,0.0,10.0);
     		vrp = vec3(0.0,0.0,-1.0);
     		upv = vec3(0.0,1.0,0.0);
	  };
	  
	      if (camIndex == 4){
        // hier wird die fünfte Kameraposition definiert
		    eye = vec3(12.0,12.0,4.0);
     		vrp = vec3(0.0,4.0,0.0);
     		upv = vec3(0.0,1.0,0.0);
	  };

    // hier wird die Viewmatrix unter Verwendung einer Hilfsfunktion berechnet,
    // die in einem externen Javascript (MV.js) definiert wird
    view = lookAt(eye, vrp, upv);  
    
    // die errechnete Viewmatrix wird an die Shader übergeben
    // die Funktion flatten löst dabei die eigentlichen Daten aus dem Javascript-Array-Objekt
    gl.uniformMatrix4fv( gl.getUniformLocation(program, "viewMatrix"), false, flatten(view) );

    // nachdem die inneren Parameter gesetzt wurden, werden nun die äußeren Parameter gesetzt
    // dazu wird die Projektionmatrix mit einer Hilfsfunktion aus einem externen Javascript (MV.js)
    // definiert
    // der Field-of-View wird auf 60° gesetzt, das Seitenverhältnis ist 1:1 (d.h. das Bild ist quadratisch),
    // die near-Plane hat den Abstand 0.01 von der Kamera und die far-Plane den Abstand 100
    projection = perspective(60.0, 1.0, 0.01, 100.0);
    
    // die errechnete Viewmatrix wird an die Shader übergeben
    gl.uniformMatrix4fv( gl.getUniformLocation(program, "projectionMatrix"), false, flatten(projection));
    
}


//
// die Funktion spezifiziert die Lichtquellen, führt schon einen Teil der Beleuchtungsrechnung durch
// und übergibt die Werte an den Shader
// 
// der Parameter materialDiffuse ist ein vec4 und gibt die Materialfarbe für die diffuse Reflektion an
//

// Parameter wurden erweitert für die Berechnung des Lichts
function calculateLights(materialDiffuse) {
    // zunächst werden die Lichtquellen spezifiziert (bei uns gibt es eine Punktlichtquelle)

    // die Position der Lichtquelle (in Weltkoordinaten)
    var lightPosition = vec4(7.0, 7.0, 0.0, 1.0);

    // die Farbe der Lichtquelle im diffusen Licht
    var lightDiffuse = vec4(1.0, 1.0, 1.0, 1.0);

    // die Farbe der Lichtquelle im Ambienten Licht
    var lightAmbient = vec4(0.5, 0.5, 0.5, 1.0);

    // die Farbe der Lichtquelle im Specularen Licht
    var lightSpecular = vec4(1.0, 1.0, 1.0, 1.0);


    // dann wird schon ein Teil der Beleuchtungsrechnung ausgeführt - das könnte man auch im Shader machen
    // aber dort würde diese Rechnung für jeden Eckpunkt (unnötigerweise) wiederholt werden. Hier rechnen wir
    // das Produkt aus lightDiffuse und materialDiffuse einmal aus und übergeben das Resultat. Zur Multiplikation
    // der beiden Vektoren nutzen wir die Funktion mult aus einem externen Javascript (MV.js)
    var diffuseProduct = mult(lightDiffuse, materialDiffuse);
    var ambientProduct = mult(lightAmbient, materialAmbient);
    var specularProduct = mult(lightSpecular, materialSpecular);

    // die Werte für die Beleuchtungsrechnung werden an die Shader übergeben

    // Übergabe der Position der Lichtquelle
    // flatten ist eine Hilfsfunktion, wlightDiffuselightDiffuseelche die Daten aus dem Javascript - Objekt herauslöst
    gl.uniform4fv(gl.getUniformLocation(program, "lightPosition"), flatten(lightPosition));

    // Übergabe des diffuseProduct
    gl.uniform4fv(gl.getUniformLocation(program, "diffuseProduct"), flatten(diffuseProduct));

    // Übergabe des ambientProduct
    gl.uniform4fv(gl.getUniformLocation(program, "ambientProduct"), flatten(ambientProduct));

    // Übergabe des specularProduct
    gl.uniform4fv(gl.getUniformLocation(program, "specularProduct"), flatten(specularProduct));

}

// Zeichnet die Textur, legt die Werte fest und wie oft diese auf eine Fläche gezeichnet wid 
//         ___
//  b 0,2 |  //| c 2,2
//        | // |
//  a 0,0 |//  | d 2,0        a,b,c,d    imageArray nach dem Muster definiert
function drawTexture(){
    var texcoordPos = gl.getAttribLocation(program, "vTexCoord");
	var buf = gl.createBuffer();
    gl.bindBuffer(gl.ARRAY_BUFFER, buf);
    gl.enableVertexAttribArray(texcoordPos);
    gl.vertexAttribPointer(texcoordPos, 2, gl.FLOAT, false, 0, 0);
	gl.bufferData(gl.ARRAY_BUFFER,imageArray,gl.STATIC_DRAW);
}



//
// Die Funktion setzt die Szene zusammen, dort wird ein Objekt nach dem anderen gezeichnet
// 
function displayScene(){

    
    //
    // Die Kamera für das Bild wird gesetzt
    
    // View-Matrix und Projection-Matrix zur Kamera berechnen
    setCamera();
    
    // ------------------------ Erster Würfel -------------------------------------------------------------------------------------------------------- Erster Würfel ----- W1
    //
    // Zeichnen des ersten Objekts (Würfel)
    
    // zunächst werden die Daten für die globalen Arrays gelöscht
    // dies ist auch schon beim ersten Objekt zu tun, denn aus den
    // Berechnungen eines früheren Frames könnten hier schon Werte in den Arrays stehen
    // auch die Anzahl der Eckpunkte des zu zeichnenden Objekts wird auf 0 zurückgesetzt
    
    numVertices = 0;
	  pointsArray.length=0;
	  colorsArray.length=0;
	  normalsArray.length=0;
    
    
    // jetzt werden die Arrays mit der entsprechenden Zeichenfunktion mit Daten gefüllt
    drawCube();
	
    // es wird festgelegt, ob eine Beleuchtungsrechnung für das Objekt durchgeführt wird oder nicht
    var lighting = false; // Beleuchtungsrechnung wird durchgeführt
    var cartoon = false;
    
    // die Information über die Beleuchtungsrechnung wird an die Shader weitergegeben
    gl.uniform1i(gl.getUniformLocation(program, "lighting"),lighting);
    gl.uniform1i(gl.getUniformLocation(program, "cartoon"),cartoon);
    
    if (lighting) {
        // es soll also eine Beleuchtungsrechnung durchgeführt werden
        
        // die Materialfarbe für diffuse Reflektion wird spezifiziert
	      var materialDiffuse = vec4( 1.0, 0.8, 0.0, 1.0);
    
        // die Beleuchtung wird durchgeführt und das Ergebnis an den Shader übergeben
        calculateLights( materialDiffuse );
         
    }
    
    
    // es muss noch festgelegt werden, wo das Objekt sich in Weltkoordinaten befindet,
    // d.h. die Model-Matrix muss errechnet werden. Dazu werden wieder Hilfsfunktionen
    // für die Matrizenrechnung aus dem externen Javascript MV.js verwendet
   
   // Initialisierung mit der Einheitsmatrix 
	 model = mat4();    
   
   // Das Objekt wird am Ende noch um die x-Achse rotiert 
   model = mult(model, rotate(theta[0], [1, 0, 0] ));
    
   // Zuvor wird das Objekt um die y-Achse rotiert
   model = mult(model, rotate(theta[1], [0, 1, 0] ));
    
   // Als erstes wird das Objekt um die z-Achse rotiert 
   model = mult(model, rotate(theta[2], [0, 0, 1] ));
   
     //gl2 a) Translation (5,0,1);
   model = mult(model, translate(5, 0, 1));
	
	// Erster Würfel dreht sich um seine eigene Z-achse
   model = mult(model, rotate(selfAxis, [0, 0, 1] ));
	
   // die Model-Matrix ist fertig berechnet und wird an die Shader übergeben 
 	 gl.uniformMatrix4fv( gl.getUniformLocation(program, "modelMatrix"), false, flatten(model) );
    
   // jetzt wird noch die Matrix errechnet, welche die Normalen transformiert
   normalMat = mat4();
   normalMat = mult( view, model );
   normalMat = inverse( normalMat );
   normalMat = transpose( normalMat );
    
   // die Normal-Matrix ist fertig berechnet und wird an die Shader übergeben 
 	 gl.uniformMatrix4fv( gl.getUniformLocation(program, "normalMatrix"), false, flatten(normalMat) );

   // schließlich wird alles gezeichnet. Dabei wird der Vertex-Shader numVertices mal aufgerufen
   // und dabei die jeweiligen attribute - Variablen für jeden einzelnen Vertex gesetzt
   // außerdem wird OpenGL mitgeteilt, dass immer drei Vertices zu einem Dreieck im Rasterisierungsschritt
   // zusammengesetzt werden sollen
   gl.drawArrays( gl.TRIANGLES, 0, numVertices );
   
    // ------------------------ Zweiter Würfel -------------------------------------------------------------------------------------------------------- Zweiter Würfel ----- W2

	// Für Informationen über die einzelnen Schritte bitte bei "Erster Würfel" nachschauen
	   numVertices = 0;
	  pointsArray.length=0;
	  colorsArray.length=0;
	  normalsArray.length=0;
	  
    	drawTexture();
		
		gl.uniform1i(gl.getUniformLocation(program, "setImage"), true);
		
    drawCube();
	
    
    var lighting = true; // Beleuchtungsrechnung wird durchgeführt
    var cartoon = false;

    gl.uniform1i(gl.getUniformLocation(program, "lighting"),lighting);
    gl.uniform1i(gl.getUniformLocation(program, "cartoon"),cartoon);

    if (lighting) {
	    var materialDiffuse = vec4( 0.0, 1.0, 0.0, 1.0);
        calculateLights( materialDiffuse);
         
    }
    
	 model = mat4();    
  
   model = mult(model, rotate(theta[0], [1, 0, 0] ));
    
   model = mult(model, rotate(theta[1], [0, 1, 0] ));
     
   model = mult(model, rotate(theta[2], [0, 0, 1] ));
   
      model = mult(model, translate(5, 0, -3));
   
   model = mult(model, scalem(2, 2, 2));
   
	// Zweiter Würfel dreht sich um seine eigene x-achse
   model = mult(model, rotate(selfAxis*2, [1, 0, 0] ));//--------------------------------------------100

 	 gl.uniformMatrix4fv( gl.getUniformLocation(program, "modelMatrix"), false, flatten(model) );
    
   normalMat = mat4();
   normalMat = mult( view, model );
   normalMat = inverse( normalMat );
   normalMat = transpose( normalMat );

 	 gl.uniformMatrix4fv( gl.getUniformLocation(program, "normalMatrix"), false, flatten(normalMat) );
   
	
	gl.drawArrays( gl.TRIANGLES, 0, numVertices );
	
	gl.uniform1i(gl.getUniformLocation(program, "setImage"), false);
	
	// ------------------------ Erste Pyramide -------------------------------------------------------------------------------------------------------- Erste Pyramide ----- P1

	// Für Informationen über die einzelnen Schritte bitte bei "Erster Würfel" nachschauen
	   numVertices = 0;
	  pointsArray.length=0;
	  colorsArray.length=0;
	  normalsArray.length=0;
    
    drawPyramid();
    var lighting = true; // Beleuchtungsrechnung wird durchgeführt
    var cartoon = false;
    
    gl.uniform1i(gl.getUniformLocation(program, "lighting"),lighting);
    gl.uniform1i(gl.getUniformLocation(program, "cartoon"),cartoon);
    
    if (lighting) {
		var materialDiffuse = vec4(1, 1, 0.0, 1.0);
        calculateLights(materialDiffuse);
         
    }
    
	 model = mat4();    
  
   model = mult(model, rotate(theta[0], [1, 0, 0] ));
    
   model = mult(model, rotate(theta[1], [0, 1, 0] ));
     
   model = mult(model, rotate(theta[2], [0, 0, 1] ));

 	 gl.uniformMatrix4fv( gl.getUniformLocation(program, "modelMatrix"), false, flatten(model) );
    
   normalMat = mat4();
   normalMat = mult( view, model );
   normalMat = inverse( normalMat );
   normalMat = transpose( normalMat );

 	 gl.uniformMatrix4fv( gl.getUniformLocation(program, "normalMatrix"), false, flatten(normalMat) );
   gl.drawArrays( gl.TRIANGLES, 0, numVertices );
   
   // ------------------------ Zweite Pyramide -------------------------------------------------------------------------------------------------------- Zweite Pyramide -----P2

	// Für Informationen über die einzelnen Schritte bitte bei "Erster Würfel" nachschauen
	   numVertices = 0;
	  pointsArray.length=0;
	  colorsArray.length=0;
	  normalsArray.length=0;
    
    drawPyramid();
    var lighting = true; // Beleuchtungsrechnung wird durchgeführt
    var cartoon = false;
    
    gl.uniform1i(gl.getUniformLocation(program, "lighting"),lighting);
    gl.uniform1i(gl.getUniformLocation(program, "cartoon"),cartoon);
    
    if (lighting) {
        var materialDiffuse = vec4(1, 0.0, 0.0, 1.0);
        calculateLights(materialDiffuse);
    }
    
	 model = mat4();    
  
   model = mult(model, rotate(theta[0], [1, 0, 0] ));
    
   model = mult(model, rotate(theta[1], [0, 1, 0] ));
     
   model = mult(model, rotate(theta[2], [0, 0, 1] ));
   
   model = mult(model, translate(0,8,0));
   
   model = mult(model, rotate(180, [0, 0, 1]));

	gl.uniformMatrix4fv( gl.getUniformLocation(program, "modelMatrix"), false, flatten(model) );
    
   normalMat = mat4();
   normalMat = mult( view, model );
   normalMat = inverse( normalMat );
   normalMat = transpose( normalMat );

 	 gl.uniformMatrix4fv( gl.getUniformLocation(program, "normalMatrix"), false, flatten(normalMat) );
   gl.drawArrays( gl.TRIANGLES, 0, numVertices );
   
      // ------------------------ Dritte Pyramide -------------------------------------------------------------------------------------------------------- Dritte Pyramide -----P3

	// Für Informationen über die einzelnen Schritte bitte bei "Erster Würfel" nachschauen
	   numVertices = 0;
	  pointsArray.length=0;
	  colorsArray.length=0;
	  normalsArray.length=0;
    
    drawPyramid();
    var lighting = true; // Beleuchtungsrechnung wird ,
    var cartoon = false;
    
    gl.uniform1i(gl.getUniformLocation(program, "lighting"),lighting);
    gl.uniform1i(gl.getUniformLocation(program, "cartoon"),cartoon);
    
    if (lighting) {
        var materialDiffuse = vec4(0.0, 0.0, 1.0, 1.0);
        calculateLights(materialDiffuse);
         
    }
    
	 model = mat4();    
  
   model = mult(model, rotate(theta[0], [1, 0, 0] ));
    
   model = mult(model, rotate(theta[1], [0, 1, 0] ));
     
   model = mult(model, rotate(theta[2], [0, 0, 1] ));
   
   model = mult(model, translate(0,6.4,0.5));
   
   model = mult(model, rotate(104, [1, 0, 0]));
   
   model = mult(model, scalem(0.4,0.4,0.4));

	gl.uniformMatrix4fv( gl.getUniformLocation(program, "modelMatrix"), false, flatten(model) );
    
   normalMat = mat4();
   normalMat = mult( view, model );
   normalMat = inverse( normalMat );
   normalMat = transpose( normalMat );

 	 gl.uniformMatrix4fv( gl.getUniformLocation(program, "normalMatrix"), false, flatten(normalMat) );
   gl.drawArrays( gl.TRIANGLES, 0, numVertices );
   
	// -------------------- Teapot ------------------------------------------------------------------------------------------------------------------------------------------------------ Teapot --------------------T1
	
	   // gl.uniform1i(gl.getUniformLocation(program, "isCartoon"), isCartoon);

    numVertices = 0;
    pointsArray.length = 0;
    colorsArray.length = 0;
    normalsArray.length = 0;
	
    drawTeapot();

    var lighting = true; // Beleuchtungsrechnung wird durchgeführt
    var cartoon = true;

    gl.uniform1i(gl.getUniformLocation(program, "lighting"), lighting);
    gl.uniform1i(gl.getUniformLocation(program, "cartoon"),cartoon);

    if (lighting) {
		
        var materialDiffuse = vec4(0.0, 0.0, 1.0, 1.0);
        calculateLights(materialDiffuse);
    }
	
    model = mat4();
	
	model = mult(model, rotate(theta[0], [1, 0, 0] ));
    
	model = mult(model, rotate(theta[1], [0, 1, 0] ));
     
	model = mult(model, rotate(theta[2], [0, 0, 1] ));

    model = mult(model, translate(-5.0, 0.0, 6.0));

    model = mult(model, scalem(0.3, 0.3, 0.3));

    model = mult(model, rotate(tearotation, [0, 1, 0]));

    gl.uniformMatrix4fv(gl.getUniformLocation(program, "modelMatrix"), false, flatten(model));

    normalMat = mat4();
    normalMat = mult(view, model);
    normalMat = inverse(normalMat);
    normalMat = transpose(normalMat);

    gl.uniformMatrix4fv(gl.getUniformLocation(program, "normalMatrix"), false, flatten(normalMat));

    gl.drawElements(gl.TRIANGLES, teapotVertexIndexBuffer.numItems, gl.UNSIGNED_SHORT, 0);

    gl.uniform1i(gl.getUniformLocation(program, "isCartoon"), false);
	
} // Ende der Funktion displayScene()


//
// hier wird eine namenslose Funktion definiert, die durch die Variable render zugegriffen werden kann.
// diese Funktion wird für jeden Frame aufgerufen
//


var render = function(){

	// Frames pro Sekunde 
	// basierend auf Annahme 20 Frames in bestimmter Zeit --> Zeit /20 ->> Zeit Pro Frame --> 1 sek / zeit pro Frame
	if(counter%20===0){
		var now=Date.now()/1000;
		document.getElementById('fps').innerHTML="FPS: "+(1/((now-then)/20)).toFixed(2);
		then=Date.now()/1000;
	}
    
    // den Framebuffer (hier wird das Bild hineingeschrieben) und den z-Buffer (wird für Verdeckungsrechnung benötigt)
    // initialisieren.
    gl.clear( gl.COLOR_BUFFER_BIT | gl.DEPTH_BUFFER_BIT);
    
    
    // Durchführung der Animation: der Würfel wird um 2° weiter gedreht und zwar um die aktuell ausgewählte Achse
	if(isOn){
    theta[axis] += 2.0;
	}
	selfAxis+=0.6; // Ausgehend von 60 FPS === 600 Frames pro 10 sek// 360 / 600 = 0.6 Intervalle
	tearotation  += 1.1;
            
    // jetzt kann die Szene gezeichnet werden
    displayScene();
      
     // Zähler für die Frames
	counter++;
	   
    // der Frame fertig gezeichnet ist, wird veranlasst, dass der nächste Frame gezeichnet wird. Dazu wird wieder
    // die die Funktion aufgerufen, welche durch die Variable render spezifiziert wird
     requestAnimFrame(render);
}


/*** Funktionen zur Ausführung von WebGL  */


//
// Diese Funktion wird beim Laden der HTML-Seite ausgeführt. Sie ist so etwas wie die "main"-Funktion
// Ziel ist es, WebGL zu initialisieren
//

window.onload = function init() {
    
    // die Referenz auf die Canvas, d.h. den Teil des Browserfensters, in den WebGL zeichnet, 
    // wird ermittelt (über den Bezeichner in der HTML-Seite)
    canvas = document.getElementById( "gl-canvas" );
    
    // über die Canvas kann man sich den WebGL-Kontext ermitteln, über den dann die OpenGL-Befehle
    // ausgeführt werden
    gl = WebGLUtils.setupWebGL( canvas );
    if ( !gl ) { alert( "WebGL isn't available" ); }

    // allgemeine Einstellungen für den Viewport (wo genau das Bild in der Canvas zu sehen ist und
    // wie groß das Bild ist)
    gl.viewport( 0, 0, canvas.width, canvas.height );
  
    // die Hintergrundfarbe wird festgelegt
    gl.clearColor( 0.9, 0.9, 1.0, 1.0 );
    
    // die Verdeckungsrechnung wird eingeschaltet: Objekte, die näher an der Kamera sind verdecken
    // Objekte, die weiter von der Kamera entfernt sind
    gl.enable(gl.DEPTH_TEST);

    // der Vertex-Shader und der Fragment-Shader werden initialisiert
    program = initShaders( gl, "vertex-shader", "fragment-shader" );
    
    // die über die Refenz "program" zugänglichen Shader werden aktiviert
    gl.useProgram( program );
	
	request.open("GET", "Teapot.json");
	request.send();
    
    // die Callbacks für das Anklicken der Buttons wird festgelegt
    // je nachdem, ob man den x-Achsen, y-Achsen oder z-Achsen-Button klickt, hat
    // axis einen anderen Wert
    document.getElementById("ButtonX").onclick = function(){axis = 0;};
    document.getElementById("ButtonY").onclick = function(){axis = 1;};
    document.getElementById("ButtonZ").onclick = function(){axis = 2;};
	document.getElementById("ButtonT").onclick = function(){isOn=!isOn};
   	
	// texture erstellen
    gl.bindTexture(gl.TEXTURE_2D, texture);
	texture = gl.createTexture();

		// Beim laden des Bildes wird die Textur von OpenGL eingelesen und eine MipMap erstellt
    image.addEventListener('load', function() {
		// Texture einlesen als 2D
        gl.bindTexture(gl.TEXTURE_2D, texture);
        gl.texImage2D(gl.TEXTURE_2D, 0, gl.RGBA, gl.RGBA, gl.UNSIGNED_BYTE, image);
		// MipMap generien
        gl.generateMipmap(gl.TEXTURE_2D);
    });

    image.src = document.getElementById("texImage").src;
	
		// jetzt kann mit dem Rendern der Szene begonnen werden  
    render();
}

