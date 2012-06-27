function pad(num) {
	if (num < 10) {
		num = "0" + num;
	}
	return num;
}

function round_tr(hours,min) {
	var h = hours;
	var m;

	if (min>30){
		h = hours+1;
		m = 0;
	} else {
		m = 30;
	}
	return pad(h)+":"+pad(m);
}


