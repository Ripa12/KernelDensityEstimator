package interval_tree.SqlParser;

import interval_tree.FrequentPatternMining.SupportCount.TableCount;
import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.arithmetic.*;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.SubSelect;

public abstract class AbstractParser implements IExpressionVisitor{

    protected TableCount tableCount;
    private double extractedValue; // ToDo: Only integers are considered as of now
    private String extractedColumn;
//    private boolean isInterval;
    private String currentTable;

    public AbstractParser(TableCount tableCount){
        this.tableCount = tableCount;
        this.extractedValue = 0.0;
        this.currentTable = "";
        this.extractedColumn = "";
//        this.isInterval = false;
    }

    @Override
    final public void setCurrentTable(String table){
        this.currentTable = table;
    }

    final protected String getCurrentTable(){
        return this.currentTable;
    }

    @Override
    public void visit(NullValue nullValue) {

    }

    @Override
    public void visit(Function function) {

    }

    @Override
    public void visit(SignedExpression signedExpression) {

    }

    @Override
    public void visit(JdbcParameter jdbcParameter) {

    }

    @Override
    public void visit(JdbcNamedParameter jdbcNamedParameter) {

    }

    public void visit(DoubleValue doubleValue) {
        extractedValue = doubleValue.getValue();
    }
    public void visit(LongValue longValue) {
        extractedValue = (double) longValue.getValue();
    }

    @Override
    public void visit(HexValue hexValue) {

    }

    @Override
    public void visit(DateValue dateValue) {

    }

    @Override
    public void visit(TimeValue timeValue) {

    }

    @Override
    public void visit(TimestampValue timestampValue) {

    }

    @Override
    public void visit(Parenthesis parenthesis) {

    }

    @Override
    public void visit(StringValue stringValue) {

    }

    @Override
    public void visit(Addition addition) {

    }

    @Override
    public void visit(Division division) {

    }

    @Override
    public void visit(Multiplication multiplication) {

    }

    @Override
    public void visit(Subtraction subtraction) {

    }

    protected abstract void finiteInterval(String column, double start, double end);

    @Override
    public void visit(AndExpression andExpression) {
        double start, end;
        String leftCol, rightCol;

//        isInterval = true;

        andExpression.getLeftExpression().accept(this);
        start = extractedValue;
        leftCol = extractedColumn;

        andExpression.getRightExpression().accept(this);
        end = extractedValue;
        rightCol = extractedColumn;

        // ToDo: identical columns must be part of same AND expression (i.e. A > 2 AND A < 4, not A > 2 AND B > 1 AND A < 5)
        if (leftCol.equalsIgnoreCase(rightCol)){
            // ToDo: maybe check that start is smaller than end?
            finiteInterval(rightCol, start, end);
        }
        else{
            // ToDo: no support for infinity yet...
        }

//        isInterval = false;

        // ToDo: might need to reset extractedValues here.
    }

    @Override
    public void visit(OrExpression orExpression) {

    }

    @Override
    public void visit(Between between) {

    }

    protected abstract void equalsTo(String col, double point);

    @Override
    public void visit(EqualsTo equalsTo) {
        equalsTo.getLeftExpression().accept(this);
        equalsTo.getRightExpression().accept(this);

        equalsTo(extractedColumn, extractedValue);
    }

    protected abstract void greaterThan(String col, double point);
    @Override
    public void visit(GreaterThan greaterThan) {
        greaterThan.getLeftExpression().accept(this);
        greaterThan.getRightExpression().accept(this);

        // ToDo: what if decimal?
        extractedValue -= 1;

//        if(!isInterval)
        greaterThan(extractedColumn, extractedValue);
    }

    @Override
    public void visit(GreaterThanEquals greaterThanEquals) {
        greaterThanEquals.getLeftExpression().accept(this);
        greaterThanEquals.getRightExpression().accept(this);

//        if(!isInterval)
        greaterThan(extractedColumn, extractedValue);
    }

    @Override
    public void visit(InExpression inExpression) {

    }

    @Override
    public void visit(IsNullExpression isNullExpression) {

    }

    @Override
    public void visit(LikeExpression likeExpression) {

    }

    protected abstract void MinorThan(String col, double point);
    @Override
    public void visit(MinorThan minorThan) {
        minorThan.getLeftExpression().accept(this);
        minorThan.getRightExpression().accept(this);

        extractedValue += 1;

//        if(!isInterval)
        greaterThan(extractedColumn, extractedValue);
    }

    @Override
    public void visit(MinorThanEquals minorThanEquals) {
        minorThanEquals.getLeftExpression().accept(this);
        minorThanEquals.getRightExpression().accept(this);

//        if(!isInterval)
        greaterThan(extractedColumn, extractedValue);
    }

    @Override
    public void visit(NotEqualsTo notEqualsTo) {

    }

    @Override
    public void visit(Column column) {
        extractedColumn = column.getColumnName();
        return;
    }

    @Override
    public void visit(SubSelect subSelect) {

    }

    @Override
    public void visit(CaseExpression caseExpression) {

    }

    @Override
    public void visit(WhenClause whenClause) {

    }

    @Override
    public void visit(ExistsExpression existsExpression) {

    }

    @Override
    public void visit(AllComparisonExpression allComparisonExpression) {

    }

    @Override
    public void visit(AnyComparisonExpression anyComparisonExpression) {

    }

    @Override
    public void visit(Concat concat) {

    }

    @Override
    public void visit(Matches matches) {

    }

    @Override
    public void visit(BitwiseAnd bitwiseAnd) {

    }

    @Override
    public void visit(BitwiseOr bitwiseOr) {

    }

    @Override
    public void visit(BitwiseXor bitwiseXor) {

    }

    @Override
    public void visit(CastExpression castExpression) {

    }

    @Override
    public void visit(Modulo modulo) {

    }

    @Override
    public void visit(AnalyticExpression analyticExpression) {

    }

    @Override
    public void visit(WithinGroupExpression withinGroupExpression) {

    }

    @Override
    public void visit(ExtractExpression extractExpression) {

    }

    @Override
    public void visit(IntervalExpression intervalExpression) {

    }

    @Override
    public void visit(OracleHierarchicalExpression oracleHierarchicalExpression) {

    }

    @Override
    public void visit(RegExpMatchOperator regExpMatchOperator) {

    }

    @Override
    public void visit(JsonExpression jsonExpression) {

    }

    @Override
    public void visit(JsonOperator jsonOperator) {

    }

    @Override
    public void visit(RegExpMySQLOperator regExpMySQLOperator) {

    }

    @Override
    public void visit(UserVariable userVariable) {

    }

    @Override
    public void visit(NumericBind numericBind) {

    }

    @Override
    public void visit(KeepExpression keepExpression) {

    }

    @Override
    public void visit(MySQLGroupConcat mySQLGroupConcat) {

    }

    @Override
    public void visit(RowConstructor rowConstructor) {

    }

    @Override
    public void visit(OracleHint oracleHint) {

    }

    @Override
    public void visit(TimeKeyExpression timeKeyExpression) {

    }

    @Override
    public void visit(DateTimeLiteralExpression dateTimeLiteralExpression) {

    }

    @Override
    public void visit(NotExpression notExpression) {

    }
}
