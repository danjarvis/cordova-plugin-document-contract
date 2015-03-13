Document Contract
=================

Android document / media provider plugin for [Cordova](http://cordova.apache.org)

Installation
------------

Install through the Cordova plugin repository:

`cordova plugin add com.danjarvis.document-contract`

Install directly from github:

`cordova plugin add https://github.com/danjarvis/document-contract.git`


Methods
-------

The plugin exposes two functions for working with a document/media provider.


**getContract(params, success, error);**

```js
getContract({
    uri: 'content://com.foo.bar/xyz',  // content URI (required)
    columns: ['_display_name']         // projection of columns (optional)
  },
  success,                             // success callback
  error);                              // error callback
```

Resolves a content URI and retrieves a contract object.

params:
```js
```

An Object of key/value pairs will get passed to the `success` callback (keys will be`columns` that returned a value).

If `columns` is not specified, the query will return all available columns. This is not recommended as it is [inefficient](http://developer.android.com/reference/android/content/ContentResolver.html#query%28android.net.Uri,%20java.lang.String[],%20java.lang.String,%20java.lang.String[],%20java.lang.String%29).


Documentation for columns of interest:

- [Document](https://developer.android.com/reference/android/provider/DocumentsContract.Document.html)
- [Media](http://developer.android.com/reference/android/provider/MediaStore.MediaColumns.html)
- [Images](http://developer.android.com/reference/android/provider/MediaStore.Images.ImageColumns.html)



**getData(params, success, callback);**

```js
getData({
    uri: 'content://com.foo.bar/xyz'	// content URI (required)
  },
  success,                              // success callback
  error);                               // error callback
```

Resolves a content URI and retrieves corresponding file data.

An [`ArrayBuffer`](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/ArrayBuffer) will get passed to the `success` callback, which can be written out to a file.


Usage
-----

For the below examples, assume that

1. `contentUri` is valid Content URI (e.g. coming from a Google Drive Send Intent).
2. [org.apache.cordova.file](http://plugins.cordova.io/#/package/org.apache.cordova.file) plugin is installed.

Get a full contract:
```js
window.plugins.DocumentContract.getContract({
    uri: contentUri
  },
  function(contract) {
    console.dir(contract);
	// Outputs
	// {
	//   '_display_name': 'SampleFile.pdf',
	//   'document_id': 'foo123',
	//   'last_modified': '/SomeDate/',
	//   'mime_type': 'application/pdf',
	//   'nth key': 'nth value'
	// }
  },
  function(error) {
    console.log('Error getting contract: ' + error);
  }
);
```

Get a specific contract:
```js
window.plugins.DocumentContract.getContract({
    uri: contentUri,
	columns: [
	  '_display_name', 'mime_type', '_size'
	]
  },
  function(contract) {
    console.dir(contract);
	// Outputs
	// {
	//   '_display_name': 'SampleFile.pdf',
	//   '_size': '5242880',
	//   'mime_type': 'application/pdf'
	// }
  },
  function(error) {
    console.log('Error getting contract: ' + error);
  }
);
```

Get file name and create file:
```js
window.plugins.DocumentContract.getContract({
    uri: contentUri,
	columns: [
	  '_display_name'
	]
  },
  function(contract) {
    window.plugins.DocumentContract.getData({
        uri: contentUri
	  },
	  function(data) {
        resolveFileSystemURL(
          cordova.file.dataDirectory,
		  function(dirEntry) {
		    dirEntry.getFile(
              contract['_display_name'],
			  {create: true, exclusive: false},
			  function(fileEntry) {
			    fileEntry.createWriter(
                  function(writer) {
				    writer.onerror = function(e) {
					  console.log('oh dear.');
					};
					writer.onwriteend = function() {
					  console.log('File created at: ' + fileEntry.nativeURL);
					};
					writer.write(data);
				  }
				);
			  }
			);
		  }
		);
	  },
	  function(error) {
        console.log('Error getting contract: ' + error);
	  }
    );
  },
  function(error) {
    console.log('Error getting contract: ' + error);
  }
);
```

Note: the above example is written to be concise -- don't make a pyramid like this.


License
-------

&copy; Dan Jarvis 2015, [MIT](http://danjarvis.mit-license.org)
