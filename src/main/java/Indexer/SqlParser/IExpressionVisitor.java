package Indexer.SqlParser;

import net.sf.jsqlparser.expression.ExpressionVisitor;

public interface IExpressionVisitor extends ExpressionVisitor {
    void setCurrentTable(String table);
    void before();
    void after();
}
