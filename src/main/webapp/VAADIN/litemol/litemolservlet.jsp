<%-- 
    Document   : litemolservlet
    Created on : Dec 8, 2017, 1:45:35 PM
    Author     : Yehia Farag
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0">
        <title>LiteMol</title>
        <!-- Complied & minified library css -->
        <link rel="stylesheet" href="//www.ebi.ac.uk/pdbe/pdb-component-library/v1.0/css/pdb.component.library.min-1.0.0.css" />

        <!-- Dependencey scripts (these can be skipped if already included in page) -->
        <link rel="stylesheet" href="style.css">

        <!-- Dependencey scripts (these can be skipped if already included in page) -->
        <script src="//www.ebi.ac.uk/pdbe/pdb-component-library/libs/d3.min.js"></script>
        <script src="//www.ebi.ac.uk/pdbe/pdb-component-library/libs/angular.1.4.7.min.js"></script>

        <!-- Complied & minified library JS -->
        <script src="//www.ebi.ac.uk/pdbe/pdb-component-library/v1.0/js/pdb.component.library.min-1.0.0.js"></script>
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>








    </head>
    <body id="litemolbody" >
    <center id="container">

        <pdb-lite-mol id="litemolid"  pdb-id= "'1cbs'" >      
        </pdb-lite-mol>
    </center>   
    <div> 
        <input id="loadnewproteinbtn" type="button" value="ahaha"  style="
               width: 100px;
               height: 100px;
               top: 400px !important;
               position: absolute;
               z-index: 900000000;
               "/></div> 

    <script>
        $(document).ready(function () {
            $("#loadnewproteinbtn").click(function () {
                liteMolScope.LiteMolComponent.moleculeId = "5exw";
                liteMolScope.LiteMolComponent.loadMolecule();

            });
        });

        function showMessage(msg) {
            alert(msg);
        }
        ;


        //variable to store LiteMol Component Scope which has all the methods
        var liteMolScope;
//Bootstrapping / Installing the library
        var bindPdbComponentScope = function (element) {
            return angular.element(element).isolateScope();
        };

        //bind to the component scope on window.onload
        window.onload = function (e) {
            //Method to bind component scope
            var litemolElement = document.getElementById('litemolid');

            liteMolScope = bindPdbComponentScope(litemolElement);
            // liteMolScope.LiteMolComponent.hideControls();
            liteMolScope.LiteMolComponent.setBackground();
        }

        var selectionDetails = {
            entity_id: '1',
            struct_asym_id: 'A',
            start_residue_number: 100,
            end_residue_number: 110,
            visualParams: {
                polymer: true,
                polymerRef: 'polymer-visual',
                het: false,
                hetRef: 'het-visual',
                water: false,
                waterRef: 'water-visual'
            }
        };
        var callSelectFocus = function () {
            var colorCode = {r: 200, g: 0, b: 0};
            var showSideChains = false;
            liteMolScope.LiteMolComponent.SelectExtractFocus(selectionDetails, colorCode, showSideChains);


        }

        function finalizeStyle() {
            var x = document.getElementsByClassName("lm-btn lm-btn-link lm-btn-link-toggle-off")[2];
            alert(x)
            x.setAttribute("style", " display:none !important; visibility:hidden;");
            var x1 = document.getElementsByClassName("lm-btn lm-btn-link lm-btn-link-toggle-on")[0];
            x1.setAttribute("style", " display:none !important; visibility:hidden;");
            callSelectFocus();
        }
        function getParameterByName(name, url) {
            if (!url)
                url = window.location.href;
            name = name.replace(/[\[\]]/g, "\\$&");
            var regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)"),
                    results = regex.exec(url);
            if (!results)
                return null;
            if (!results[2])
                return '';
            return decodeURIComponent(results[2].replace(/\+/g, " "));
        }
        angular.element(document).ready(function () {
            angular.bootstrap(document, ['pdb.component.library']);
        });

        function loadNewProtein(pdb) {          
            totestbtn(pdb);
            document.getElementById("loadnewproteinbtn").click();

        }
        function totestbtn(pdb) {  
            alert("invoker");
            liteMolScope.LiteMolComponent.moleculeId = pdb;
            liteMolScope.LiteMolComponent.loadMolecule();


        }

    </script>    
</body>
</html>
