// Generated from AntlrDsl.g4 by ANTLR 4.6

package migrator.bench.parser;

import java.util.List;

import org.antlr.v4.runtime.NoViableAltException;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.RuntimeMetaData;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.Vocabulary;
import org.antlr.v4.runtime.VocabularyImpl;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class AntlrDslParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.6", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, MEM=6, LNOT=7, LAND=8, LOR=9,
		JOIN=10, SEL=11, PROJ=12, INS=13, DEL=14, UPD=15, LOP=16, ID=17, WS=18;
	public static final int
		RULE_stmtRoot = 0, RULE_stmt = 1, RULE_queryExpr = 2, RULE_insStmt = 3,
		RULE_delStmt = 4, RULE_updStmt = 5, RULE_attrList = 6, RULE_tuple = 7,
		RULE_pred = 8;
	public static final String[] ruleNames = {
		"stmtRoot", "stmt", "queryExpr", "insStmt", "delStmt", "updStmt", "attrList",
		"tuple", "pred"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'('", "','", "')'", "'['", "']'", "'in'", "'not'", "'and'", "'or'",
		"'join'", "'sigma'", "'pi'", "'ins'", "'del'", "'upd'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, "MEM", "LNOT", "LAND", "LOR", "JOIN",
		"SEL", "PROJ", "INS", "DEL", "UPD", "LOP", "ID", "WS"
	};
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "AntlrDsl.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public AntlrDslParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class StmtRootContext extends ParserRuleContext {
		public StmtContext stmt() {
			return getRuleContext(StmtContext.class,0);
		}
		public TerminalNode EOF() { return getToken(AntlrDslParser.EOF, 0); }
		public StmtRootContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_stmtRoot; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof AntlrDslVisitor ) return ((AntlrDslVisitor<? extends T>)visitor).visitStmtRoot(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StmtRootContext stmtRoot() throws RecognitionException {
		StmtRootContext _localctx = new StmtRootContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_stmtRoot);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(18);
			stmt();
			setState(19);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class StmtContext extends ParserRuleContext {
		public StmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_stmt; }

		public StmtContext() { }
		public void copyFrom(StmtContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class ToInsStmtContext extends StmtContext {
		public InsStmtContext insStmt() {
			return getRuleContext(InsStmtContext.class,0);
		}
		public ToInsStmtContext(StmtContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof AntlrDslVisitor ) return ((AntlrDslVisitor<? extends T>)visitor).visitToInsStmt(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ToQueryExprContext extends StmtContext {
		public QueryExprContext queryExpr() {
			return getRuleContext(QueryExprContext.class,0);
		}
		public ToQueryExprContext(StmtContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof AntlrDslVisitor ) return ((AntlrDslVisitor<? extends T>)visitor).visitToQueryExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ToDelStmtContext extends StmtContext {
		public DelStmtContext delStmt() {
			return getRuleContext(DelStmtContext.class,0);
		}
		public ToDelStmtContext(StmtContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof AntlrDslVisitor ) return ((AntlrDslVisitor<? extends T>)visitor).visitToDelStmt(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ToUpdStmtContext extends StmtContext {
		public UpdStmtContext updStmt() {
			return getRuleContext(UpdStmtContext.class,0);
		}
		public ToUpdStmtContext(StmtContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof AntlrDslVisitor ) return ((AntlrDslVisitor<? extends T>)visitor).visitToUpdStmt(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StmtContext stmt() throws RecognitionException {
		StmtContext _localctx = new StmtContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_stmt);
		try {
			setState(25);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case JOIN:
			case SEL:
			case PROJ:
			case ID:
				_localctx = new ToQueryExprContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(21);
				queryExpr();
				}
				break;
			case INS:
				_localctx = new ToInsStmtContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(22);
				insStmt();
				}
				break;
			case DEL:
				_localctx = new ToDelStmtContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(23);
				delStmt();
				}
				break;
			case UPD:
				_localctx = new ToUpdStmtContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(24);
				updStmt();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class QueryExprContext extends ParserRuleContext {
		public QueryExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_queryExpr; }

		public QueryExprContext() { }
		public void copyFrom(QueryExprContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class ToIdContext extends QueryExprContext {
		public TerminalNode ID() { return getToken(AntlrDslParser.ID, 0); }
		public ToIdContext(QueryExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof AntlrDslVisitor ) return ((AntlrDslVisitor<? extends T>)visitor).visitToId(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ProjExprContext extends QueryExprContext {
		public TerminalNode PROJ() { return getToken(AntlrDslParser.PROJ, 0); }
		public AttrListContext attrList() {
			return getRuleContext(AttrListContext.class,0);
		}
		public QueryExprContext queryExpr() {
			return getRuleContext(QueryExprContext.class,0);
		}
		public ProjExprContext(QueryExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof AntlrDslVisitor ) return ((AntlrDslVisitor<? extends T>)visitor).visitProjExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class JoinExprContext extends QueryExprContext {
		public TerminalNode JOIN() { return getToken(AntlrDslParser.JOIN, 0); }
		public List<QueryExprContext> queryExpr() {
			return getRuleContexts(QueryExprContext.class);
		}
		public QueryExprContext queryExpr(int i) {
			return getRuleContext(QueryExprContext.class,i);
		}
		public JoinExprContext(QueryExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof AntlrDslVisitor ) return ((AntlrDslVisitor<? extends T>)visitor).visitJoinExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class SelExprContext extends QueryExprContext {
		public TerminalNode SEL() { return getToken(AntlrDslParser.SEL, 0); }
		public PredContext pred() {
			return getRuleContext(PredContext.class,0);
		}
		public QueryExprContext queryExpr() {
			return getRuleContext(QueryExprContext.class,0);
		}
		public SelExprContext(QueryExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof AntlrDslVisitor ) return ((AntlrDslVisitor<? extends T>)visitor).visitSelExpr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final QueryExprContext queryExpr() throws RecognitionException {
		QueryExprContext _localctx = new QueryExprContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_queryExpr);
		try {
			setState(49);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case JOIN:
				_localctx = new JoinExprContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(27);
				match(JOIN);
				setState(28);
				match(T__0);
				setState(29);
				queryExpr();
				setState(30);
				match(T__1);
				setState(31);
				queryExpr();
				setState(32);
				match(T__2);
				}
				break;
			case SEL:
				_localctx = new SelExprContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(34);
				match(SEL);
				setState(35);
				match(T__0);
				setState(36);
				pred();
				setState(37);
				match(T__1);
				setState(38);
				queryExpr();
				setState(39);
				match(T__2);
				}
				break;
			case PROJ:
				_localctx = new ProjExprContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(41);
				match(PROJ);
				setState(42);
				match(T__0);
				setState(43);
				attrList();
				setState(44);
				match(T__1);
				setState(45);
				queryExpr();
				setState(46);
				match(T__2);
				}
				break;
			case ID:
				_localctx = new ToIdContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(48);
				match(ID);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class InsStmtContext extends ParserRuleContext {
		public TerminalNode INS() { return getToken(AntlrDslParser.INS, 0); }
		public TerminalNode ID() { return getToken(AntlrDslParser.ID, 0); }
		public TupleContext tuple() {
			return getRuleContext(TupleContext.class,0);
		}
		public InsStmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_insStmt; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof AntlrDslVisitor ) return ((AntlrDslVisitor<? extends T>)visitor).visitInsStmt(this);
			else return visitor.visitChildren(this);
		}
	}

	public final InsStmtContext insStmt() throws RecognitionException {
		InsStmtContext _localctx = new InsStmtContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_insStmt);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(51);
			match(INS);
			setState(52);
			match(T__0);
			setState(53);
			match(ID);
			setState(54);
			match(T__1);
			setState(55);
			tuple();
			setState(56);
			match(T__2);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class DelStmtContext extends ParserRuleContext {
		public TerminalNode DEL() { return getToken(AntlrDslParser.DEL, 0); }
		public TerminalNode ID() { return getToken(AntlrDslParser.ID, 0); }
		public PredContext pred() {
			return getRuleContext(PredContext.class,0);
		}
		public DelStmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_delStmt; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof AntlrDslVisitor ) return ((AntlrDslVisitor<? extends T>)visitor).visitDelStmt(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DelStmtContext delStmt() throws RecognitionException {
		DelStmtContext _localctx = new DelStmtContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_delStmt);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(58);
			match(DEL);
			setState(59);
			match(T__0);
			setState(60);
			match(ID);
			setState(61);
			match(T__1);
			setState(62);
			pred();
			setState(63);
			match(T__2);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class UpdStmtContext extends ParserRuleContext {
		public TerminalNode UPD() { return getToken(AntlrDslParser.UPD, 0); }
		public List<TerminalNode> ID() { return getTokens(AntlrDslParser.ID); }
		public TerminalNode ID(int i) {
			return getToken(AntlrDslParser.ID, i);
		}
		public PredContext pred() {
			return getRuleContext(PredContext.class,0);
		}
		public UpdStmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_updStmt; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof AntlrDslVisitor ) return ((AntlrDslVisitor<? extends T>)visitor).visitUpdStmt(this);
			else return visitor.visitChildren(this);
		}
	}

	public final UpdStmtContext updStmt() throws RecognitionException {
		UpdStmtContext _localctx = new UpdStmtContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_updStmt);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(65);
			match(UPD);
			setState(66);
			match(T__0);
			setState(67);
			match(ID);
			setState(68);
			match(T__1);
			setState(69);
			pred();
			setState(70);
			match(T__1);
			setState(71);
			match(ID);
			setState(72);
			match(T__1);
			setState(73);
			match(ID);
			setState(74);
			match(T__2);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class AttrListContext extends ParserRuleContext {
		public List<TerminalNode> ID() { return getTokens(AntlrDslParser.ID); }
		public TerminalNode ID(int i) {
			return getToken(AntlrDslParser.ID, i);
		}
		public AttrListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_attrList; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof AntlrDslVisitor ) return ((AntlrDslVisitor<? extends T>)visitor).visitAttrList(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AttrListContext attrList() throws RecognitionException {
		AttrListContext _localctx = new AttrListContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_attrList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(76);
			match(T__3);
			setState(77);
			match(ID);
			setState(82);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__1) {
				{
				{
				setState(78);
				match(T__1);
				setState(79);
				match(ID);
				}
				}
				setState(84);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(85);
			match(T__4);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TupleContext extends ParserRuleContext {
		public List<TerminalNode> ID() { return getTokens(AntlrDslParser.ID); }
		public TerminalNode ID(int i) {
			return getToken(AntlrDslParser.ID, i);
		}
		public TupleContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tuple; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof AntlrDslVisitor ) return ((AntlrDslVisitor<? extends T>)visitor).visitTuple(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TupleContext tuple() throws RecognitionException {
		TupleContext _localctx = new TupleContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_tuple);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(87);
			match(T__0);
			setState(88);
			match(ID);
			setState(93);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__1) {
				{
				{
				setState(89);
				match(T__1);
				setState(90);
				match(ID);
				}
				}
				setState(95);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(96);
			match(T__2);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class PredContext extends ParserRuleContext {
		public PredContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_pred; }

		public PredContext() { }
		public void copyFrom(PredContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class InPredContext extends PredContext {
		public TerminalNode MEM() { return getToken(AntlrDslParser.MEM, 0); }
		public TerminalNode ID() { return getToken(AntlrDslParser.ID, 0); }
		public QueryExprContext queryExpr() {
			return getRuleContext(QueryExprContext.class,0);
		}
		public InPredContext(PredContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof AntlrDslVisitor ) return ((AntlrDslVisitor<? extends T>)visitor).visitInPred(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class AndPredContext extends PredContext {
		public TerminalNode LAND() { return getToken(AntlrDslParser.LAND, 0); }
		public List<PredContext> pred() {
			return getRuleContexts(PredContext.class);
		}
		public PredContext pred(int i) {
			return getRuleContext(PredContext.class,i);
		}
		public AndPredContext(PredContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof AntlrDslVisitor ) return ((AntlrDslVisitor<? extends T>)visitor).visitAndPred(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class NotPredContext extends PredContext {
		public TerminalNode LNOT() { return getToken(AntlrDslParser.LNOT, 0); }
		public PredContext pred() {
			return getRuleContext(PredContext.class,0);
		}
		public NotPredContext(PredContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof AntlrDslVisitor ) return ((AntlrDslVisitor<? extends T>)visitor).visitNotPred(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class LopPredContext extends PredContext {
		public List<TerminalNode> ID() { return getTokens(AntlrDslParser.ID); }
		public TerminalNode ID(int i) {
			return getToken(AntlrDslParser.ID, i);
		}
		public TerminalNode LOP() { return getToken(AntlrDslParser.LOP, 0); }
		public LopPredContext(PredContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof AntlrDslVisitor ) return ((AntlrDslVisitor<? extends T>)visitor).visitLopPred(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class OrPredContext extends PredContext {
		public TerminalNode LOR() { return getToken(AntlrDslParser.LOR, 0); }
		public List<PredContext> pred() {
			return getRuleContexts(PredContext.class);
		}
		public PredContext pred(int i) {
			return getRuleContext(PredContext.class,i);
		}
		public OrPredContext(PredContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof AntlrDslVisitor ) return ((AntlrDslVisitor<? extends T>)visitor).visitOrPred(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PredContext pred() throws RecognitionException {
		PredContext _localctx = new PredContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_pred);
		try {
			setState(127);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case LNOT:
				_localctx = new NotPredContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(98);
				match(LNOT);
				setState(99);
				match(T__0);
				setState(100);
				pred();
				setState(101);
				match(T__2);
				}
				break;
			case LAND:
				_localctx = new AndPredContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(103);
				match(LAND);
				setState(104);
				match(T__0);
				setState(105);
				pred();
				setState(106);
				match(T__1);
				setState(107);
				pred();
				setState(108);
				match(T__2);
				}
				break;
			case LOR:
				_localctx = new OrPredContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(110);
				match(LOR);
				setState(111);
				match(T__0);
				setState(112);
				pred();
				setState(113);
				match(T__1);
				setState(114);
				pred();
				setState(115);
				match(T__2);
				}
				break;
			case MEM:
				_localctx = new InPredContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(117);
				match(MEM);
				setState(118);
				match(T__0);
				setState(119);
				match(ID);
				setState(120);
				match(T__1);
				setState(121);
				queryExpr();
				setState(122);
				match(T__2);
				}
				break;
			case ID:
				_localctx = new LopPredContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(124);
				match(ID);
				setState(125);
				match(LOP);
				setState(126);
				match(ID);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3\24\u0084\4\2\t\2"+
		"\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\3\2\3"+
		"\2\3\2\3\3\3\3\3\3\3\3\5\3\34\n\3\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4"+
		"\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\5\4\64\n\4\3\5\3"+
		"\5\3\5\3\5\3\5\3\5\3\5\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\7\3\7\3\7\3\7\3\7"+
		"\3\7\3\7\3\7\3\7\3\7\3\7\3\b\3\b\3\b\3\b\7\bS\n\b\f\b\16\bV\13\b\3\b\3"+
		"\b\3\t\3\t\3\t\3\t\7\t^\n\t\f\t\16\ta\13\t\3\t\3\t\3\n\3\n\3\n\3\n\3\n"+
		"\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3"+
		"\n\3\n\3\n\3\n\3\n\3\n\3\n\5\n\u0082\n\n\3\n\2\2\13\2\4\6\b\n\f\16\20"+
		"\22\2\2\u0086\2\24\3\2\2\2\4\33\3\2\2\2\6\63\3\2\2\2\b\65\3\2\2\2\n<\3"+
		"\2\2\2\fC\3\2\2\2\16N\3\2\2\2\20Y\3\2\2\2\22\u0081\3\2\2\2\24\25\5\4\3"+
		"\2\25\26\7\2\2\3\26\3\3\2\2\2\27\34\5\6\4\2\30\34\5\b\5\2\31\34\5\n\6"+
		"\2\32\34\5\f\7\2\33\27\3\2\2\2\33\30\3\2\2\2\33\31\3\2\2\2\33\32\3\2\2"+
		"\2\34\5\3\2\2\2\35\36\7\f\2\2\36\37\7\3\2\2\37 \5\6\4\2 !\7\4\2\2!\"\5"+
		"\6\4\2\"#\7\5\2\2#\64\3\2\2\2$%\7\r\2\2%&\7\3\2\2&\'\5\22\n\2\'(\7\4\2"+
		"\2()\5\6\4\2)*\7\5\2\2*\64\3\2\2\2+,\7\16\2\2,-\7\3\2\2-.\5\16\b\2./\7"+
		"\4\2\2/\60\5\6\4\2\60\61\7\5\2\2\61\64\3\2\2\2\62\64\7\23\2\2\63\35\3"+
		"\2\2\2\63$\3\2\2\2\63+\3\2\2\2\63\62\3\2\2\2\64\7\3\2\2\2\65\66\7\17\2"+
		"\2\66\67\7\3\2\2\678\7\23\2\289\7\4\2\29:\5\20\t\2:;\7\5\2\2;\t\3\2\2"+
		"\2<=\7\20\2\2=>\7\3\2\2>?\7\23\2\2?@\7\4\2\2@A\5\22\n\2AB\7\5\2\2B\13"+
		"\3\2\2\2CD\7\21\2\2DE\7\3\2\2EF\7\23\2\2FG\7\4\2\2GH\5\22\n\2HI\7\4\2"+
		"\2IJ\7\23\2\2JK\7\4\2\2KL\7\23\2\2LM\7\5\2\2M\r\3\2\2\2NO\7\6\2\2OT\7"+
		"\23\2\2PQ\7\4\2\2QS\7\23\2\2RP\3\2\2\2SV\3\2\2\2TR\3\2\2\2TU\3\2\2\2U"+
		"W\3\2\2\2VT\3\2\2\2WX\7\7\2\2X\17\3\2\2\2YZ\7\3\2\2Z_\7\23\2\2[\\\7\4"+
		"\2\2\\^\7\23\2\2][\3\2\2\2^a\3\2\2\2_]\3\2\2\2_`\3\2\2\2`b\3\2\2\2a_\3"+
		"\2\2\2bc\7\5\2\2c\21\3\2\2\2de\7\t\2\2ef\7\3\2\2fg\5\22\n\2gh\7\5\2\2"+
		"h\u0082\3\2\2\2ij\7\n\2\2jk\7\3\2\2kl\5\22\n\2lm\7\4\2\2mn\5\22\n\2no"+
		"\7\5\2\2o\u0082\3\2\2\2pq\7\13\2\2qr\7\3\2\2rs\5\22\n\2st\7\4\2\2tu\5"+
		"\22\n\2uv\7\5\2\2v\u0082\3\2\2\2wx\7\b\2\2xy\7\3\2\2yz\7\23\2\2z{\7\4"+
		"\2\2{|\5\6\4\2|}\7\5\2\2}\u0082\3\2\2\2~\177\7\23\2\2\177\u0080\7\22\2"+
		"\2\u0080\u0082\7\23\2\2\u0081d\3\2\2\2\u0081i\3\2\2\2\u0081p\3\2\2\2\u0081"+
		"w\3\2\2\2\u0081~\3\2\2\2\u0082\23\3\2\2\2\7\33\63T_\u0081";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}