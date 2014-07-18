package jjs.common.adtclasses.classes;

public class PropertyInfoW {

	public enum ComparisonOperator {
		EQUALS, NOT_EQUALS, GREATER_THAN, LESS_THAN, GREATER_THAN_OR_EQUALS, LESS_THAN_OR_EQUALS, LIKE, NULL, NOT_NULL
	}

	private String alias;

	private String name;

	private Object value;

	private ComparisonOperator comparisonoper;

	private String[] compString = { "=", "<>", ">", "<", ">=", "<=", "like", "is null", "is not null" };

	public String getOperator() {
		return compString[comparisonoper.ordinal()];
	}

	public boolean isUnaryOperator() {
		return comparisonoper == ComparisonOperator.NULL || comparisonoper == ComparisonOperator.NOT_NULL;
	}

	public PropertyInfoW(String name, String alias, Object value) {
		this.name = name;
		this.alias = alias;
		this.value = value;
		this.comparisonoper = ComparisonOperator.EQUALS;
	}

	public PropertyInfoW(String name, String alias, Object value, ComparisonOperator comparisonoper) {
		this.name = name;
		this.alias = alias;
		this.value = value;
		this.comparisonoper = comparisonoper;
	}

	public String getAlias() {
		return alias;
	}

	public String getName() {
		return name;
	}

	public Object getValue() {
		return value;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public ComparisonOperator getComparisonOperator() {
		return comparisonoper;
	}

	public void setComparisonOperator(ComparisonOperator comparisonoper) {
		this.comparisonoper = comparisonoper;
	}

}
