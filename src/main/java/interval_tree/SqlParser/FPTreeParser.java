package interval_tree.SqlParser;

import interval_tree.DataStructure.IntervalTree;
import interval_tree.FrequentPatternMining.FPTree;
import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.arithmetic.*;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.SubSelect;

import java.util.*;

/**
 * Created by Richard on 2018-03-04.
 */
public class FPTreeParser implements ExpressionVisitor {

    private FPTree fpTree;

    private Map<String, Integer> supportCount;

    private int extractedValue; // ToDo: Only integers are considered as of now

    private String extractedColumn;

    private boolean isInterval;

    private TreeMap<String, IntervalTree.NodeData> list;

    public FPTreeParser(Map<String, Integer> supportCount){
        this.supportCount = supportCount;
        extractedValue = 0;
        extractedColumn = "";
        isInterval = false;

        list = new TreeMap<>();
    }

    public void parse(Expression exp){
        list.clear();
        exp.accept(this);
        //list.sort(Comparator.comparingInt(supportCount::get));
        //list.removeIf(x -> supportCount.get(x.getKey()) < 3); ToDo: Filter infrequent items here!
        fpTree.insertTree(list.keySet(), (IntervalTree.NodeData[]) list.values().toArray());
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
            list.put(rightCol, new IntervalTree.Interval(start, end));
        }
        else{
            // ToDo: no support for infinity yet...
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

        list.put(extractedColumn, new IntervalTree.Point(extractedValue));
    }

    public void visit(GreaterThan greaterThan) {

        greaterThan.getLeftExpression().accept(this);
        greaterThan.getRightExpression().accept(this);

        // ToDo: what if decimal?
        extractedValue += 1;

    }

    public void visit(GreaterThanEquals greaterThanEquals) {

        greaterThanEquals.getLeftExpression().accept(this);
        greaterThanEquals.getRightExpression().accept(this);
    }

    public void visit(MinorThan minorThan) {

        minorThan.getLeftExpression().accept(this);
        minorThan.getRightExpression().accept(this);

        extractedValue -= 1;
    }

    public void visit(MinorThanEquals minorThanEquals) {

        minorThanEquals.getLeftExpression().accept(this);
        minorThanEquals.getRightExpression().accept(this);
    }

    // ToDo: no support for yet, maybe separate estimator for != and -INF > A < INF
    public void visit(NotEqualsTo notEqualsTo) {

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

    }
    public void visit(Addition addition) {

    }
    public void visit(Division division) {

    }
    public void visit(Multiplication multiplication) {

    }
    public void visit(Subtraction subtraction) {

    }
    public void visit(InExpression inExpression) {

    }
    public void visit(IsNullExpression isNullExpression) {

    }
    public void visit(LikeExpression likeExpression) {

    }
}
