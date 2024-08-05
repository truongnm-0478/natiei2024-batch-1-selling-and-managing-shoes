var head = document.getElementsByTagName('head')[0];

var link = document.createElement('link');
link.rel = 'stylesheet';
link.href = 'https://cdn.jsdelivr.net/npm/bootstrap@4.1.3/dist/css/bootstrap.min.css';
link.integrity = "sha384-MCw98/SFnGE8fJT3GXwEOngsV7Zt27NXFoaoApmYm81iuXoPkFOJwJ8ERdknLPMO";
link.crossOrigin = "anonymous"
head.appendChild(link);

var script1 = document.createElement('script');
script1.src = 'https://code.jquery.com/jquery-3.3.1.slim.min.js';
script1.integrity = "sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo";
script1.crossOrigin = "anonymous";
head.appendChild(script1);

var script2 = document.createElement('script');
script2.src = 'https://cdn.jsdelivr.net/npm/popper.js@1.14.3/dist/umd/popper.min.js';
script2.integrity = "sha384-ZMP7rVo3mIykV+2+9J3UJ46jBk0WLaUAdn689aCwoqbBJiSnjAK/l8WvCWPIPm49";
script2.crossOrigin = "anonymous";
head.appendChild(script2);

var script3 = document.createElement('script');
script3.src = 'https://cdn.jsdelivr.net/npm/bootstrap@4.1.3/dist/js/bootstrap.min.js';
script3.integrity = "sha384-ChfqqxuZUCnJSK3+MXmPNIyE6ZbWh2IMqE241rYiqJxyMiZ6OW/JmZQ5stwEULTy";
script3.crossOrigin = "anonymous";
head.appendChild(script3);
