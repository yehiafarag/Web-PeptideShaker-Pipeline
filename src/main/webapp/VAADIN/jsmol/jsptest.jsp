

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8">


        <title>JSmol Panel</title>



        <link rel="stylesheet" type="text/css" href="VAADIN/jsmol/style/style.css">
        <script type="text/javascript" src="VAADIN/jsmol/JSmol.min.js"></script>
        <script type="text/javascript">

            Jmol._isAsync = false;

            // last update 2/18/2014 2:10:06 PM

            var jsmol; // set up in HTML table, below

            // logic is set by indicating order of USE -- default is HTML5 for this test page, though

            var s = document.location.search;

            // Developers: The _debugCode flag is checked in j2s/core/core.z.js, 
            // and, if TRUE, skips loading the core methods, forcing those
            // to be read from their individual directories. Set this
            // true if you want to do some code debugging by inserting
            // System.out.println, document.title, or alert commands
            // anywhere in the Java or Jmol code.

            Jmol._debugCode = (s.indexOf("debugcode") >= 0);
            /* var pdb = '1BLU';decodeURIComponent(getParameterByName('pdb'));*/
            
            jmol_isReady = function (applet) {
                Jmol._getElement(applet, "appletdiv").style.border = "none";
                Jmol._getElement(applet, "appletdiv").style.width = '100%';
                Jmol._getElement(applet, "appletdiv").style.height = '100%';
                Jmol._getElement(applet, "appletinfotablediv").style.border = "none";                
                Jmol._getElement(applet, "appletinfotablediv").style.height = '100%';
                Jmol._getElement(applet, "appletinfotablediv").style.width = '100%';



            }
            function loadNewProtein(pdb) {
                Jmol.search(jsmol, '=' + pdb, 'spin on; ribbon only; background white; select all ;color lightgray');
            }
            function excutequery(query) {
                Jmol.script(jsmol, query);

            }
            function selectChain(chainid, start, end) {
                Jmol.script(jsmol, 'select  resno >=' + start + ' and resno <= ' + end + 'chain = ' + chainid + ' ; color green');
            }
            function selectePeptide(start, end, color) {
                Jmol.clearConsole(jsmol);
                Jmol.script(jsmol, 'select select resno >=' + start + ' and resno <= ' + end + '; color ' + color);
            }


            /**"select resno >=" + (peptideTempStart - chains[selectedChainIndex - 1].getDifference())
             + " and resno <=" + (peptideTempEnd - chains[selectedChainIndex - 1].getDifference())
             + " and chain = " + currentChain + "; color green")*/

            var Info = {
                debug: false,
                color: "0xFFFFFF",
                addSelectionOptions: true,
                use: "HTML5", // JAVA HTML5 WEBGL are all options
                j2sPath: "VAADIN/jsmol/j2s", // this needs to point to where the j2s directory is.
                jarPath: "./java", // this needs to point to where the java directory is.
                jarFile: "JmolAppletSigned.jar",
                isSigned: true,
                script: "set zoomlarge false;set antialiasDisplay;",
                serverURL: "http://chemapps.stolaf.edu/jmol/jsmol/php/jsmol.php",
                readyFunction: jmol_isReady,
                disableJ2SLoadMonitor: true,
                disableInitialConsole: true,
                allowJavaScript: true,
                spinning: true
                //defaultModel: "$dopamine",
                //console: "none", // default will be jsmol_infodiv, but you can designate another div here or "none"
            }

            $(document).ready(function () {
                $("#appdiv").html(Jmol.getAppletHtml("jsmol", Info));
                document.getElementById('jmolApplet0_canvas2d').style.width = '50%';
                document.getElementById('jsmol_submit').style.border = 'none';

            })
            var lastPrompt = 0;

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
        </script>
    </head>
    <body id="outerhtml">
        <table>
            <tr><td  >
            <center>    
                <div id="appdiv" style="width:100%;height:100%"></div> 
            </center>
        </td>
    </tr>
</table>
</body>
</html>
