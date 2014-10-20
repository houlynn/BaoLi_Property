Ext.define('core.test.view.MainController', {
	   extend: 'Ext.app.ViewController',  
	    requires: [  
	        'Ext.MessageBox'  
	    ],  
	  
	    alias: 'controller.main',  
	  
	    onClickButton: function () {  
	        Ext.Msg.confirm('Confirm', 'Are you sure?', 'onConfirm', this); 
	        
	    },  
	  
	    onConfirm: function (choice) {  
	        if (choice === 'yes') {  
	            //  
	        	 this.getView().getViewModel().set('name' , "修改后的title");  
	        }  
	    }  
	});  