// Generated from AntlrDsl.g4 by ANTLR 4.6

package migrator.bench.parser;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link AntlrDslParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface AntlrDslVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link AntlrDslParser#stmtRoot}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStmtRoot(AntlrDslParser.StmtRootContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ToQueryExpr}
	 * labeled alternative in {@link AntlrDslParser#stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitToQueryExpr(AntlrDslParser.ToQueryExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ToInsStmt}
	 * labeled alternative in {@link AntlrDslParser#stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitToInsStmt(AntlrDslParser.ToInsStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ToDelStmt}
	 * labeled alternative in {@link AntlrDslParser#stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitToDelStmt(AntlrDslParser.ToDelStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ToUpdStmt}
	 * labeled alternative in {@link AntlrDslParser#stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitToUpdStmt(AntlrDslParser.ToUpdStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code JoinExpr}
	 * labeled alternative in {@link AntlrDslParser#queryExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJoinExpr(AntlrDslParser.JoinExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code SelExpr}
	 * labeled alternative in {@link AntlrDslParser#queryExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelExpr(AntlrDslParser.SelExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ProjExpr}
	 * labeled alternative in {@link AntlrDslParser#queryExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProjExpr(AntlrDslParser.ProjExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ToId}
	 * labeled alternative in {@link AntlrDslParser#queryExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitToId(AntlrDslParser.ToIdContext ctx);
	/**
	 * Visit a parse tree produced by {@link AntlrDslParser#insStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInsStmt(AntlrDslParser.InsStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link AntlrDslParser#delStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDelStmt(AntlrDslParser.DelStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link AntlrDslParser#updStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUpdStmt(AntlrDslParser.UpdStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link AntlrDslParser#attrList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAttrList(AntlrDslParser.AttrListContext ctx);
	/**
	 * Visit a parse tree produced by {@link AntlrDslParser#tuple}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTuple(AntlrDslParser.TupleContext ctx);
	/**
	 * Visit a parse tree produced by the {@code NotPred}
	 * labeled alternative in {@link AntlrDslParser#pred}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNotPred(AntlrDslParser.NotPredContext ctx);
	/**
	 * Visit a parse tree produced by the {@code AndPred}
	 * labeled alternative in {@link AntlrDslParser#pred}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAndPred(AntlrDslParser.AndPredContext ctx);
	/**
	 * Visit a parse tree produced by the {@code OrPred}
	 * labeled alternative in {@link AntlrDslParser#pred}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOrPred(AntlrDslParser.OrPredContext ctx);
	/**
	 * Visit a parse tree produced by the {@code InPred}
	 * labeled alternative in {@link AntlrDslParser#pred}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInPred(AntlrDslParser.InPredContext ctx);
	/**
	 * Visit a parse tree produced by the {@code LopPred}
	 * labeled alternative in {@link AntlrDslParser#pred}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLopPred(AntlrDslParser.LopPredContext ctx);
}