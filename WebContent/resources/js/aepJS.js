/**
 * funciones globales javascrips AEP
 */

PrimeFaces.locales['es'] = {
    closeText: 'Cerrar',
    prevText: 'Anterior',
    nextText: 'Siguiente',
    monthNames: ['Enero','Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio', 'Julio', 'Agosto', 'Septiembre', 'Octubre', 'Noviembre', 'Diciembre'],
    monthNamesShort: ['Ene', 'Feb', 'Mar', 'Abr', 'May', 'Jun','Jul','Ago','Sep','Oct','Nov','Dic'],
    dayNames: ['Domingo','Lunes','Martes','Miércoles','Jueves','Viernes','Sábado'],
    dayNamesShort: ['Dom','Lun', 'Mar', 'Mie', 'Jue', 'Vie', 'Sab'],
    dayNamesMin: ['D','L','M','Mi','J','V','S'],
    weekHeader: 'Semana',
    firstDay: 1,
    isRTL: false,
    showMonthAfterYear: false,
    yearSuffix: '',
    timeOnlyTitle: 'Sólo hora',
    timeText: 'Tiempo',
    hourText: 'Hora',
    minuteText: 'Minuto',
    secondText: 'Segundo',
    currentText: 'Fecha actual',
    ampm: false,
    month: 'Mes',
    week: 'Semana',
    day: 'Día',
    allDayText : 'Todo el día'
};
function bloquearScroll(){
	document.body.style.overflow = 'hidden';
}

function habilitarScroll(){
	document.body.style.overflow = 'visible';
}
function borderColor(id) {
    document.getElementById(id).style.border = "red";
}

function validacionTr(){
	 Swal.fire("Información Mandatoria", "Debe completar los campos obligatorios en rojo.", "warning");
}

function validacion(){
    Swal.fire("Información Mandatoria", "Debe completar los campos obligatorios en rojo.", "warning");
}

function validaRun(rut,tipo) {
	if(tipo=="NACIONAL"){  
	    var nPos = 0; 
	    var sInvertido = ""; 
	    var sRut = "";
	    for(var i = rut.length - 1; i >= 0; i-- ){
	        sInvertido += rut.charAt(i);
	        if (i == rut.length - 1 )
	            sInvertido += "-";
	        else if (nPos == 3)
	        {
	            sInvertido += ".";
	            nPos = 0;
	        }
	        nPos++;
	    }
	    for(var j = sInvertido.length - 1; j >= 0; j-- ){
	        if (sInvertido.charAt(sInvertido.length - 1) != ".")
	            sRut += sInvertido.charAt(j);
	        else if (j != sInvertido.length - 1 )
	            sRut += sInvertido.charAt(j);
	    }
	    alert("paso");
	    return sRut.toUpperCase();
	}else{
		alert("EXTE");
		return rut;
	}

}
function borderColorMandatorio(objet) {
	if(objet.value==null){
		document.getElementById(objet.id).style.border = "red";
	} 
   
}