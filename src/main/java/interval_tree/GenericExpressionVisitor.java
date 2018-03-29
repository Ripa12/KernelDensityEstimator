package interval_tree;

import interval_tree.MyIntervalTree;
import javafx.beans.binding.IntegerExpression;
import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.arithmetic.*;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.SubSelect;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Richard on 2018-03-04.
 */
public class GenericExpressionVisitor implements ExpressionVisitor {

    private Map<String, MyIntervalTree> intervalTrees;

    private int extractedValue; // ToDo: Only integers are considered as of now

    private String extractedColumn;

    private boolean isInterval;


    public GenericExpressionVisitor(Map<String, MyIntervalTree> trees){
        intervalTrees = trees;
        extractedValue = 0;
        extractedColumn = "";
        isInterval = false;
    }

    public void visit(NullValue nullValue) {

    }

    public void visit(Function function) {

    }

    public void visit(SignedExpression signedExpression) {

    }

    public void visit(JdbcParameter jdbcParameter) {

    }

    public void visit(JdbcNamedParameter jdbcNamedParameter) {

    }

    public void visit(DoubleValue doubleValue) {
        extractedValue = (int)doubleValue.getValue();
    }

    public void visit(LongValue longValue) {
        extractedValue = (int)longValue.getValue();
    }

    public void visit(HexValue hexValue) {

    }

    public void visit(DateValue dateValue) {

    }

    public void visit(TimeValue timeValue) {

    }

    public void visit(TimestampValue timestampValue) {

    }

    public void visit(Parenthesis parenthesis) {

    }

    // ToDo: Strings are out of scope, ignore!
    public void visit(StringValue stringValue) {
        //System.out.println("String: " + stringValue.getValue());
        //extractedColumn = stringValue.getValue();
    }

    public void visit(Addition addition) {

    }

    public void visit(Division division) {

    }

    public void visit(Multiplication multiplication) {

    }

    public void visit(Subtraction subtraction) {

    }

    public void visit(AndExpression andExpression) {
        int start, end;
        String leftCol, rightCol;

        isInterval = true;

        andExpression.getLeftExpression().accept(this);
        start = extractedValue;
        leftCol = extractedColumn;

        andExpression.getRightExpression().accept(this);
        end = extractedValue;
        rightCol = extractedColumn;

        // ToDo: identical columns must be part of same AND expression (i.e. A > 2 AND A < 4, not A > 2 AND B > 1 AND A < 5)
        if (leftCol.equalsIgnoreCase(rightCol)){
            // ToDo: maybe check that start is smaller than end?
            intervalTrees.get(rightCol).insert(new MyIntervalTree.Interval(start, end));
        }
        else{
            // ToDo: no support for infinity yet...
            //intervalTrees.get(leftCol).insert(new MyIntervalTree.Point(start));

            //intervalTrees.get(rightCol).insert(new MyIntervalTree.Point(end));
        }

        isInterval = false;

    }

    public void visit(OrExpression orExpression) {
        // ToDo: library does not seem to support Or Expression for the moment
//        process(OrExpression);
//        process(OrExpression.getRightExpression());
    }

    public void visit(Between between) {

    }

    public void visit(EqualsTo equalsTo) {
        equalsTo.getLeftExpression().accept(this);
        equalsTo.getRightExpression().accept(this);

        intervalTrees.get(extractedColumn).insert(new MyIntervalTree.Point(extractedValue));
    }

    public void visit(GreaterThan greaterThan) {
        //process(greaterThan.getLeftExpression());
        //process(greaterThan.getRightExpression());

        greaterThan.getLeftExpression().accept(this);
        greaterThan.getRightExpression().accept(this);

        // ToDo: what if decimal?
        extractedValue += 1;

    }

    public void visit(GreaterThanEquals greaterThanEquals) {
        //process(greaterThanEquals.getLeftExpression());
        //process(greaterThanEquals.getRightExpression());

        greaterThanEquals.getLeftExpression().accept(this);
        greaterThanEquals.getRightExpression().accept(this);
    }

    public void visit(InExpression inExpression) {

    }

    public void visit(IsNullExpression isNullExpression) {

    }

    public void visit(LikeExpression likeExpression) {

    }

    public void visit(MinorThan minorThan) {
        //process(minorThan.getLeftExpression());
        //process(minorThan.getRightExpression());
        minorThan.getLeftExpression().accept(this);
        minorThan.getRightExpression().accept(this);

        extractedValue -= 1;
    }

    public void visit(MinorThanEquals minorThanEquals) {
        //process(minorThanEquals.getLeftExpression());
        //process(minorThanEquals.getRightExpression());

        minorThanEquals.getLeftExpression().accept(this);
        minorThanEquals.getRightExpression().accept(this);
    }

    // ToDo: no support for yet, maybe separate estimator for != and -INF > A < INF
    public void visit(NotEqualsTo notEqualsTo) {
        //process(notEqualsTo.getLeftExpression());
        //process(notEqualsTo.getRightExpression());
    }

    public void visit(Column column) {
        //System.out.println("Attribute: " + column.getColumnName());
        extractedColumn = column.getColumnName();
    }

    public void visit(SubSelect subSelect) {

    }

    public void visit(CaseExpression caseExpression) {

    }

    public void visit(WhenClause whenClause) {

    }

    public void visit(ExistsExpression existsExpression) {

    }

    public void visit(AllComparisonExpression allComparisonExpression) {

    }

    public void visit(AnyComparisonExpression anyComparisonExpression) {

    }

    public void visit(Concat concat) {

    }

    public void visit(Matches matches) {

    }

    public void visit(BitwiseAnd bitwiseAnd) {

    }

    public void visit(BitwiseOr bitwiseOr) {

    }

    public void visit(BitwiseXor bitwiseXor) {

    }

    public void visit(CastExpression castExpression) {

    }

    public void visit(Modulo modulo) {

    }

    public void visit(AnalyticExpression analyticExpression) {

    }

    public void visit(WithinGroupExpression withinGroupExpression) {

    }

    public void visit(ExtractExpression extractExpression) {

    }

    public void visit(IntervalExpression intervalExpression) {

    }

    public void visit(OracleHierarchicalExpression oracleHierarchicalExpression) {

    }

    public void visit(RegExpMatchOperator regExpMatchOperator) {

    }

    public void visit(JsonExpression jsonExpression) {

    }

    public void visit(JsonOperator jsonOperator) {

    }

    public void visit(RegExpMySQLOperator regExpMySQLOperator) {

    }

    public void visit(UserVariable userVariable) {

    }

    public void visit(NumericBind numericBind) {

    }

    public void visit(KeepExpression keepExpression) {

    }

    public void visit(MySQLGroupConcat mySQLGroupConcat) {

    }

    public void visit(RowConstructor rowConstructor) {

    }

    public void visit(OracleHint oracleHint) {

    }

    public void visit(TimeKeyExpression timeKeyExpression) {

    }

    public void visit(DateTimeLiteralExpression dateTimeLiteralExpression) {

    }

    public void visit(NotExpression notExpression) {

    }
}
