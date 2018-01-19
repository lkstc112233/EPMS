package obj.annualTable.list;

public class List<L extends Node<?,?>> extends Node<Object,L> {
	
	public List() {
		super(Object.class);
	}
	
	public L get(Object TofListContent) {
		for(L content:this.getList()) {
			if(TofListContent==null && content.getT()==null) return content;
			if(TofListContent!=null && content.getT()!=null) {
				if(TofListContent.equals(content.getT()))
					return content;
			}
		}
		return null;
	}
	
}
