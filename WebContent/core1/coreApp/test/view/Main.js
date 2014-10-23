Ext.define('core.test.view.Main', {
    extend: 'Ext.container.Container',
    xtype: 'app-main',  
    requires:['core.test.view.MainController','core.test.model.MainModel'], 
    controller: 'main',  
   viewModel: {  
        type: 'main'  
    },  
    layout: {  
        type: 'border'  
    },  
  
    items: [{  
        xtype: 'panel',  
        bind: {  
           title: '{name}'  
        },  
        region: 'west',  
        html: '<ul>...</ul>',  
        width: 250,  
        split: true,  
        tbar: [{  
            text: 'Button',  
           handler: 'onClickButton'  
        }]  
    },{  
        region: 'center',  
        xtype: 'tabpanel',  
        items:[{  
            title: 'Tab 1',  
            html: '<h2>Content ...</h2>'  
        }]  
    }]  
});  