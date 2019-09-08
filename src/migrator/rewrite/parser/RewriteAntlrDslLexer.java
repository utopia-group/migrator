// Generated from RewriteAntlrDsl.g4 by ANTLR 4.6

package migrator.rewrite.parser;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class RewriteAntlrDslLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.6", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, IN=9, 
		AND=10, OR=11, NOT=12, SELECT=13, UPDATE=14, INSERT=15, DELETE=16, AS=17, 
		ON=18, SET=19, FROM=20, INTO=21, JOIN=22, WHERE=23, VALUES=24, CREATE=25, 
		TABLE=26, PRIMARY=27, FOREIGN=28, KEY=29, REFERENCES=30, QUERY=31, FRESH=32, 
		EQ=33, INEQ=34, ID=35, NUM=36, WS=37;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", "T__7", "A", "B", 
		"C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", 
		"Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "IN", "AND", "OR", "NOT", 
		"SELECT", "UPDATE", "INSERT", "DELETE", "AS", "ON", "SET", "FROM", "INTO", 
		"JOIN", "WHERE", "VALUES", "CREATE", "TABLE", "PRIMARY", "FOREIGN", "KEY", 
		"REFERENCES", "QUERY", "FRESH", "EQ", "INEQ", "ID", "NUM", "WS"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "','", "';'", "'('", "')'", "'?'", "'->'", "'{'", "'}'", null, null, 
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, null, null, null, null, null, null, "'='"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, null, null, null, "IN", "AND", "OR", 
		"NOT", "SELECT", "UPDATE", "INSERT", "DELETE", "AS", "ON", "SET", "FROM", 
		"INTO", "JOIN", "WHERE", "VALUES", "CREATE", "TABLE", "PRIMARY", "FOREIGN", 
		"KEY", "REFERENCES", "QUERY", "FRESH", "EQ", "INEQ", "ID", "NUM", "WS"
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


	public RewriteAntlrDslLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "RewriteAntlrDsl.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2\'\u016d\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t"+
		"\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\4<\t<\4=\t="+
		"\4>\t>\4?\t?\4@\t@\3\2\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3\6\3\6\3\7\3\7\3\7"+
		"\3\b\3\b\3\t\3\t\3\n\3\n\3\13\3\13\3\f\3\f\3\r\3\r\3\16\3\16\3\17\3\17"+
		"\3\20\3\20\3\21\3\21\3\22\3\22\3\23\3\23\3\24\3\24\3\25\3\25\3\26\3\26"+
		"\3\27\3\27\3\30\3\30\3\31\3\31\3\32\3\32\3\33\3\33\3\34\3\34\3\35\3\35"+
		"\3\36\3\36\3\37\3\37\3 \3 \3!\3!\3\"\3\"\3#\3#\3$\3$\3$\3%\3%\3%\3%\3"+
		"&\3&\3&\3\'\3\'\3\'\3\'\3(\3(\3(\3(\3(\3(\3(\3)\3)\3)\3)\3)\3)\3)\3*\3"+
		"*\3*\3*\3*\3*\3*\3+\3+\3+\3+\3+\3+\3+\3,\3,\3,\3-\3-\3-\3.\3.\3.\3.\3"+
		"/\3/\3/\3/\3/\3\60\3\60\3\60\3\60\3\60\3\61\3\61\3\61\3\61\3\61\3\62\3"+
		"\62\3\62\3\62\3\62\3\62\3\63\3\63\3\63\3\63\3\63\3\63\3\63\3\64\3\64\3"+
		"\64\3\64\3\64\3\64\3\64\3\65\3\65\3\65\3\65\3\65\3\65\3\66\3\66\3\66\3"+
		"\66\3\66\3\66\3\66\3\66\3\67\3\67\3\67\3\67\3\67\3\67\3\67\3\67\38\38"+
		"\38\38\39\39\39\39\39\39\39\39\39\39\39\3:\3:\3:\3:\3:\3:\3;\3;\3;\3;"+
		"\3;\3;\3<\3<\3=\3=\3=\3=\3=\3=\3=\3=\5=\u0159\n=\3>\3>\7>\u015d\n>\f>"+
		"\16>\u0160\13>\3?\6?\u0163\n?\r?\16?\u0164\3@\6@\u0168\n@\r@\16@\u0169"+
		"\3@\3@\2\2A\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\2\25\2\27\2\31\2\33"+
		"\2\35\2\37\2!\2#\2%\2\'\2)\2+\2-\2/\2\61\2\63\2\65\2\67\29\2;\2=\2?\2"+
		"A\2C\2E\2G\13I\fK\rM\16O\17Q\20S\21U\22W\23Y\24[\25]\26_\27a\30c\31e\32"+
		"g\33i\34k\35m\36o\37q s!u\"w#y${%}&\177\'\3\2 \4\2CCcc\4\2DDdd\4\2EEe"+
		"e\4\2FFff\4\2GGgg\4\2HHhh\4\2IIii\4\2JJjj\4\2KKkk\4\2LLll\4\2MMmm\4\2"+
		"NNnn\4\2OOoo\4\2PPpp\4\2QQqq\4\2RRrr\4\2SSss\4\2TTtt\4\2UUuu\4\2VVvv\4"+
		"\2WWww\4\2XXxx\4\2YYyy\4\2ZZzz\4\2[[{{\4\2\\\\||\5\2C\\aac|\7\2/\60\62"+
		";C\\aac|\3\2\62;\5\2\13\f\17\17\"\"\u0159\2\3\3\2\2\2\2\5\3\2\2\2\2\7"+
		"\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2"+
		"\2\2G\3\2\2\2\2I\3\2\2\2\2K\3\2\2\2\2M\3\2\2\2\2O\3\2\2\2\2Q\3\2\2\2\2"+
		"S\3\2\2\2\2U\3\2\2\2\2W\3\2\2\2\2Y\3\2\2\2\2[\3\2\2\2\2]\3\2\2\2\2_\3"+
		"\2\2\2\2a\3\2\2\2\2c\3\2\2\2\2e\3\2\2\2\2g\3\2\2\2\2i\3\2\2\2\2k\3\2\2"+
		"\2\2m\3\2\2\2\2o\3\2\2\2\2q\3\2\2\2\2s\3\2\2\2\2u\3\2\2\2\2w\3\2\2\2\2"+
		"y\3\2\2\2\2{\3\2\2\2\2}\3\2\2\2\2\177\3\2\2\2\3\u0081\3\2\2\2\5\u0083"+
		"\3\2\2\2\7\u0085\3\2\2\2\t\u0087\3\2\2\2\13\u0089\3\2\2\2\r\u008b\3\2"+
		"\2\2\17\u008e\3\2\2\2\21\u0090\3\2\2\2\23\u0092\3\2\2\2\25\u0094\3\2\2"+
		"\2\27\u0096\3\2\2\2\31\u0098\3\2\2\2\33\u009a\3\2\2\2\35\u009c\3\2\2\2"+
		"\37\u009e\3\2\2\2!\u00a0\3\2\2\2#\u00a2\3\2\2\2%\u00a4\3\2\2\2\'\u00a6"+
		"\3\2\2\2)\u00a8\3\2\2\2+\u00aa\3\2\2\2-\u00ac\3\2\2\2/\u00ae\3\2\2\2\61"+
		"\u00b0\3\2\2\2\63\u00b2\3\2\2\2\65\u00b4\3\2\2\2\67\u00b6\3\2\2\29\u00b8"+
		"\3\2\2\2;\u00ba\3\2\2\2=\u00bc\3\2\2\2?\u00be\3\2\2\2A\u00c0\3\2\2\2C"+
		"\u00c2\3\2\2\2E\u00c4\3\2\2\2G\u00c6\3\2\2\2I\u00c9\3\2\2\2K\u00cd\3\2"+
		"\2\2M\u00d0\3\2\2\2O\u00d4\3\2\2\2Q\u00db\3\2\2\2S\u00e2\3\2\2\2U\u00e9"+
		"\3\2\2\2W\u00f0\3\2\2\2Y\u00f3\3\2\2\2[\u00f6\3\2\2\2]\u00fa\3\2\2\2_"+
		"\u00ff\3\2\2\2a\u0104\3\2\2\2c\u0109\3\2\2\2e\u010f\3\2\2\2g\u0116\3\2"+
		"\2\2i\u011d\3\2\2\2k\u0123\3\2\2\2m\u012b\3\2\2\2o\u0133\3\2\2\2q\u0137"+
		"\3\2\2\2s\u0142\3\2\2\2u\u0148\3\2\2\2w\u014e\3\2\2\2y\u0158\3\2\2\2{"+
		"\u015a\3\2\2\2}\u0162\3\2\2\2\177\u0167\3\2\2\2\u0081\u0082\7.\2\2\u0082"+
		"\4\3\2\2\2\u0083\u0084\7=\2\2\u0084\6\3\2\2\2\u0085\u0086\7*\2\2\u0086"+
		"\b\3\2\2\2\u0087\u0088\7+\2\2\u0088\n\3\2\2\2\u0089\u008a\7A\2\2\u008a"+
		"\f\3\2\2\2\u008b\u008c\7/\2\2\u008c\u008d\7@\2\2\u008d\16\3\2\2\2\u008e"+
		"\u008f\7}\2\2\u008f\20\3\2\2\2\u0090\u0091\7\177\2\2\u0091\22\3\2\2\2"+
		"\u0092\u0093\t\2\2\2\u0093\24\3\2\2\2\u0094\u0095\t\3\2\2\u0095\26\3\2"+
		"\2\2\u0096\u0097\t\4\2\2\u0097\30\3\2\2\2\u0098\u0099\t\5\2\2\u0099\32"+
		"\3\2\2\2\u009a\u009b\t\6\2\2\u009b\34\3\2\2\2\u009c\u009d\t\7\2\2\u009d"+
		"\36\3\2\2\2\u009e\u009f\t\b\2\2\u009f \3\2\2\2\u00a0\u00a1\t\t\2\2\u00a1"+
		"\"\3\2\2\2\u00a2\u00a3\t\n\2\2\u00a3$\3\2\2\2\u00a4\u00a5\t\13\2\2\u00a5"+
		"&\3\2\2\2\u00a6\u00a7\t\f\2\2\u00a7(\3\2\2\2\u00a8\u00a9\t\r\2\2\u00a9"+
		"*\3\2\2\2\u00aa\u00ab\t\16\2\2\u00ab,\3\2\2\2\u00ac\u00ad\t\17\2\2\u00ad"+
		".\3\2\2\2\u00ae\u00af\t\20\2\2\u00af\60\3\2\2\2\u00b0\u00b1\t\21\2\2\u00b1"+
		"\62\3\2\2\2\u00b2\u00b3\t\22\2\2\u00b3\64\3\2\2\2\u00b4\u00b5\t\23\2\2"+
		"\u00b5\66\3\2\2\2\u00b6\u00b7\t\24\2\2\u00b78\3\2\2\2\u00b8\u00b9\t\25"+
		"\2\2\u00b9:\3\2\2\2\u00ba\u00bb\t\26\2\2\u00bb<\3\2\2\2\u00bc\u00bd\t"+
		"\27\2\2\u00bd>\3\2\2\2\u00be\u00bf\t\30\2\2\u00bf@\3\2\2\2\u00c0\u00c1"+
		"\t\31\2\2\u00c1B\3\2\2\2\u00c2\u00c3\t\32\2\2\u00c3D\3\2\2\2\u00c4\u00c5"+
		"\t\33\2\2\u00c5F\3\2\2\2\u00c6\u00c7\5#\22\2\u00c7\u00c8\5-\27\2\u00c8"+
		"H\3\2\2\2\u00c9\u00ca\5\23\n\2\u00ca\u00cb\5-\27\2\u00cb\u00cc\5\31\r"+
		"\2\u00ccJ\3\2\2\2\u00cd\u00ce\5/\30\2\u00ce\u00cf\5\65\33\2\u00cfL\3\2"+
		"\2\2\u00d0\u00d1\5-\27\2\u00d1\u00d2\5/\30\2\u00d2\u00d3\59\35\2\u00d3"+
		"N\3\2\2\2\u00d4\u00d5\5\67\34\2\u00d5\u00d6\5\33\16\2\u00d6\u00d7\5)\25"+
		"\2\u00d7\u00d8\5\33\16\2\u00d8\u00d9\5\27\f\2\u00d9\u00da\59\35\2\u00da"+
		"P\3\2\2\2\u00db\u00dc\5;\36\2\u00dc\u00dd\5\61\31\2\u00dd\u00de\5\31\r"+
		"\2\u00de\u00df\5\23\n\2\u00df\u00e0\59\35\2\u00e0\u00e1\5\33\16\2\u00e1"+
		"R\3\2\2\2\u00e2\u00e3\5#\22\2\u00e3\u00e4\5-\27\2\u00e4\u00e5\5\67\34"+
		"\2\u00e5\u00e6\5\33\16\2\u00e6\u00e7\5\65\33\2\u00e7\u00e8\59\35\2\u00e8"+
		"T\3\2\2\2\u00e9\u00ea\5\31\r\2\u00ea\u00eb\5\33\16\2\u00eb\u00ec\5)\25"+
		"\2\u00ec\u00ed\5\33\16\2\u00ed\u00ee\59\35\2\u00ee\u00ef\5\33\16\2\u00ef"+
		"V\3\2\2\2\u00f0\u00f1\5\23\n\2\u00f1\u00f2\5\67\34\2\u00f2X\3\2\2\2\u00f3"+
		"\u00f4\5/\30\2\u00f4\u00f5\5-\27\2\u00f5Z\3\2\2\2\u00f6\u00f7\5\67\34"+
		"\2\u00f7\u00f8\5\33\16\2\u00f8\u00f9\59\35\2\u00f9\\\3\2\2\2\u00fa\u00fb"+
		"\5\35\17\2\u00fb\u00fc\5\65\33\2\u00fc\u00fd\5/\30\2\u00fd\u00fe\5+\26"+
		"\2\u00fe^\3\2\2\2\u00ff\u0100\5#\22\2\u0100\u0101\5-\27\2\u0101\u0102"+
		"\59\35\2\u0102\u0103\5/\30\2\u0103`\3\2\2\2\u0104\u0105\5%\23\2\u0105"+
		"\u0106\5/\30\2\u0106\u0107\5#\22\2\u0107\u0108\5-\27\2\u0108b\3\2\2\2"+
		"\u0109\u010a\5? \2\u010a\u010b\5!\21\2\u010b\u010c\5\33\16\2\u010c\u010d"+
		"\5\65\33\2\u010d\u010e\5\33\16\2\u010ed\3\2\2\2\u010f\u0110\5=\37\2\u0110"+
		"\u0111\5\23\n\2\u0111\u0112\5)\25\2\u0112\u0113\5;\36\2\u0113\u0114\5"+
		"\33\16\2\u0114\u0115\5\67\34\2\u0115f\3\2\2\2\u0116\u0117\5\27\f\2\u0117"+
		"\u0118\5\65\33\2\u0118\u0119\5\33\16\2\u0119\u011a\5\23\n\2\u011a\u011b"+
		"\59\35\2\u011b\u011c\5\33\16\2\u011ch\3\2\2\2\u011d\u011e\59\35\2\u011e"+
		"\u011f\5\23\n\2\u011f\u0120\5\25\13\2\u0120\u0121\5)\25\2\u0121\u0122"+
		"\5\33\16\2\u0122j\3\2\2\2\u0123\u0124\5\61\31\2\u0124\u0125\5\65\33\2"+
		"\u0125\u0126\5#\22\2\u0126\u0127\5+\26\2\u0127\u0128\5\23\n\2\u0128\u0129"+
		"\5\65\33\2\u0129\u012a\5C\"\2\u012al\3\2\2\2\u012b\u012c\5\35\17\2\u012c"+
		"\u012d\5/\30\2\u012d\u012e\5\65\33\2\u012e\u012f\5\33\16\2\u012f\u0130"+
		"\5#\22\2\u0130\u0131\5\37\20\2\u0131\u0132\5-\27\2\u0132n\3\2\2\2\u0133"+
		"\u0134\5\'\24\2\u0134\u0135\5\33\16\2\u0135\u0136\5C\"\2\u0136p\3\2\2"+
		"\2\u0137\u0138\5\65\33\2\u0138\u0139\5\33\16\2\u0139\u013a\5\35\17\2\u013a"+
		"\u013b\5\33\16\2\u013b\u013c\5\65\33\2\u013c\u013d\5\33\16\2\u013d\u013e"+
		"\5-\27\2\u013e\u013f\5\27\f\2\u013f\u0140\5\33\16\2\u0140\u0141\5\67\34"+
		"\2\u0141r\3\2\2\2\u0142\u0143\5\63\32\2\u0143\u0144\5;\36\2\u0144\u0145"+
		"\5\33\16\2\u0145\u0146\5\65\33\2\u0146\u0147\5C\"\2\u0147t\3\2\2\2\u0148"+
		"\u0149\5\35\17\2\u0149\u014a\5\65\33\2\u014a\u014b\5\33\16\2\u014b\u014c"+
		"\5\67\34\2\u014c\u014d\5!\21\2\u014dv\3\2\2\2\u014e\u014f\7?\2\2\u014f"+
		"x\3\2\2\2\u0150\u0151\7>\2\2\u0151\u0159\7@\2\2\u0152\u0159\7>\2\2\u0153"+
		"\u0154\7>\2\2\u0154\u0159\7?\2\2\u0155\u0159\7@\2\2\u0156\u0157\7@\2\2"+
		"\u0157\u0159\7?\2\2\u0158\u0150\3\2\2\2\u0158\u0152\3\2\2\2\u0158\u0153"+
		"\3\2\2\2\u0158\u0155\3\2\2\2\u0158\u0156\3\2\2\2\u0159z\3\2\2\2\u015a"+
		"\u015e\t\34\2\2\u015b\u015d\t\35\2\2\u015c\u015b\3\2\2\2\u015d\u0160\3"+
		"\2\2\2\u015e\u015c\3\2\2\2\u015e\u015f\3\2\2\2\u015f|\3\2\2\2\u0160\u015e"+
		"\3\2\2\2\u0161\u0163\t\36\2\2\u0162\u0161\3\2\2\2\u0163\u0164\3\2\2\2"+
		"\u0164\u0162\3\2\2\2\u0164\u0165\3\2\2\2\u0165~\3\2\2\2\u0166\u0168\t"+
		"\37\2\2\u0167\u0166\3\2\2\2\u0168\u0169\3\2\2\2\u0169\u0167\3\2\2\2\u0169"+
		"\u016a\3\2\2\2\u016a\u016b\3\2\2\2\u016b\u016c\b@\2\2\u016c\u0080\3\2"+
		"\2\2\7\2\u0158\u015e\u0164\u0169\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}