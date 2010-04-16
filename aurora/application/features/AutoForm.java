package aurora.application.features;

import java.io.IOException;

import uncertain.composite.CompositeMap;
import uncertain.event.Configuration;
import uncertain.event.EventModel;
import uncertain.proc.IFeature;
import uncertain.proc.ProcedureRunner;
import aurora.bm.BusinessModel;
import aurora.bm.IModelFactory;
import aurora.presentation.component.std.GridLayout;
import aurora.presentation.component.std.config.ComponentConfig;
import aurora.presentation.component.std.config.FormConfig;
import aurora.presentation.component.std.config.TextFieldConfig;
import aurora.service.ServiceContext;

public class AutoForm implements IFeature{
	private static final String PROPERTITY_HREF = "href";
	
	IModelFactory mFactory;
	CompositeMap view;

    public AutoForm(IModelFactory factory) {
        this.mFactory = factory;
    }
    
    public int onCreateView(ProcedureRunner runner ) throws IOException {
        ServiceContext sc = ServiceContext.createServiceContext(runner.getContext());
        CompositeMap model = sc.getModel();
    	
    	FormConfig formConfig = FormConfig.getInstance(view);
		formConfig.setCellspacing(0);
		
		String target = view.getString(ComponentConfig.PROPERTITY_BINDTARGET,"");
		
		String href = view.getString(PROPERTITY_HREF, "");
		if(!"".equals(href)){
			href = uncertain.composite.TextParser.parse(href, model);
			BusinessModel bm = mFactory.getModelForRead(href, "xml");
			aurora.bm.Field[] fields = bm.getFields();
			aurora.bm.QueryField[] querys = bm.getQueryFieldsArray();
			int ql = querys.length;
			int fl = fields.length;
			for(int i=0;i<ql;i++){
				aurora.bm.QueryField query = querys[i];
				for(int n=0;n<fl;n++){
					aurora.bm.Field field = fields[n];
					if(field.getName().equals(query.getName())){
						TextFieldConfig textField = TextFieldConfig.getInstance(field.getObjectContext());
						textField.setWidth(field.getFormWidth());
						if(!"".equals(target))textField.setBindTarget(target);
						formConfig.addChild(textField.getObjectContext());
						break;
					}
				}
				
			}
		}
		view.getParent().replaceChild(view, formConfig.getObjectContext());
    	return EventModel.HANDLE_NORMAL;
    }
	
	public int attachTo(CompositeMap v, Configuration procConfig) {
		view = v;
		return IFeature.NORMAL;
	}
}
