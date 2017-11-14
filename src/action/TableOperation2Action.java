package action;

import obj.*;

public abstract class TableOperation2Action extends TableOperationAction{
	private static final long serialVersionUID = -4678755399212188691L;

	private boolean[][] fieldsDisplay;
	
	public boolean[][] getFieldsDisplay(){return this.fieldsDisplay;}
	
	
	

	public TableOperation2Action(Field[] displayFields) {
		super();
		if(this.search==null || this.search.getParam()==null)
			return;
		this.fieldsDisplay=new boolean[this.search.getParam().size()][];
		for(int i=0;i<this.fieldsDisplay.length;i++){
			JoinParam.Part part=this.getSearch().getParam().getList().get(i);
			this.fieldsDisplay[i]=new boolean[Field.getFields(part.getClazz()).length];
			int j=0;
			for(Field f2:Field.getFields(part.getClazz())){
				this.fieldsDisplay[i][j]=false;
				for(Field f:displayFields)
					if(f!=null && f2.getClazz().equals(f.getClazz()) && f2.equals(f)){
						this.fieldsDisplay[i][j]=true;break;
				}j++;
			}
		}
	}
	public TableOperation2Action(){
		super();
		if(this.search==null || this.search.getParam()==null)
			return;
		this.fieldsDisplay=new boolean[this.search.getParam().size()][];
		for(int i=0;i<this.fieldsDisplay.length;i++){
			JoinParam.Part part=this.getSearch().getParam().getList().get(i);
			this.fieldsDisplay[i]=new boolean[Field.getFields(part.getClazz()).length];
			for(int j=0;j<this.fieldsDisplay[i].length;j++)
				this.fieldsDisplay[i][j]=true;
		}
	}
	
	@Override
	abstract protected Search createSearch() throws Exception;

}
