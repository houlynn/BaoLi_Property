/*
Ext.onReady(function(){
	Ext.application({
		name:"core",//引用的名称
		scope :this,
        appFolder : "core/coreApp",//应用的目录
        autoCreateViewport: 'core.app.view.main.Main' , 
       
        	
	})
});*/

/*
 * This file is generated and updated by Sencha Cmd. You can edit this file as
 * needed for your application, but these edits will have to be merged by
 * Sencha Cmd when upgrading.
 */


Ext.application({
    name: 'app',

    extend: 'app.Application',
    
    autoCreateViewport: 'app.view.main.Main'
	
    //-------------------------------------------------------------------------
    // Most customizations should be made to app.Application. If you need to
    // customize this file, doing so below this section reduces the likelihood
    // of merge conflicts when upgrading to new versions of Sencha Cmd.
    //-------------------------------------------------------------------------
});
