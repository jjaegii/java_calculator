import java.awt.*; // GridLayout, TextArea
import java.awt.event.*; // keyListener, ActionListener
import javax.swing.*; // JFrame, JPanel, JLabel
import java.util.*;

class Calculate {
	MyStack mySt; // 연산자 스택과 숫자 스택을 저장해줄 스택
	String[] postfix; // infix를 변환하여 postfix로 저장할 String 배열
	boolean isError; // Error가 있는지 확인하는 필드
	String result; // 결과를 저장해줄 String 필드

	Calculate(int arrLen, String[] infix) { // 입력받은 수식의 길이와 수식을 받아옴
		mySt = new MyStack(); // 연산자 스택과 숫자 스택을 저장해줄 스택을 초기화
		postfix = new String[arrLen]; // infix를 변환하여 postfix로 저장할 String 배열을 입력받은 수식의 길이만큼 초기화
		isError = false; // Error가 있는지 확인하는 필드를 false로 초기화

		infix2Postfix(infix); // infix를 postfix로 변환
		calculatePostfix(); // postfix를 계산
	}

	int getPriority(String ch) { // 연산자 우선순위를 반환해주는 함수
		int answer = 0;
		if (ch.equals("+") || ch.equals("-")) // +, -는 1
			answer = 1;
		if (ch.equals("*") || ch.equals("/")) // *, /는 2
			answer = 2;
		if (ch.equals("^") || ch.equals("log")) // ^, log는 3
			answer = 3;

		return answer;
	}

	void setPostfixLength() { // postfix가 저장된 배열의 길이를 경우에 따라 조정하는 함수
		int idx = 0;

		while (idx != postfix.length && postfix[idx] != null) // 원래 postfix의 길이와 작고 postfix의 저장된 데이터가 없을때까지 idx를 늘려줌
			idx++;

		if (postfix.length != idx) { // 원래 postfix의 길이가 아니라면
			String[] newPostfix = new String[idx]; // postfix의 새로운 길이를 할당한 newPostfix String 배열을 만들고
			System.arraycopy(postfix, 0, newPostfix, 0, idx); // postfix의 데이터를 복사하여 가져와서
			postfix = newPostfix; // postfix를 대체함
		}

	}

	void printWrongExp() { // 오류를 출력하는 함수
		postfix[0] = "잘못된 수식입니다.";
		isError = true; // Error가 있어 true로 바꿈
		for (int j = 1; j < postfix.length; j++)
			postfix[j] = null; // postfix 전체 삭제
		setPostfixLength(); // postfix의 길이 조정
	}

	boolean isOperator(String exp) { // 연산자(+,-,*,/,^,log)인지 확인하는 함수
		if (exp.equals("+") || exp.equals("-") || exp.equals("*") || exp.equals("/") || exp.equals("^")
				|| exp.equals("log")) {
			return true; // 연산자라면 true 반환
		} else
			return false; // 아니라면 false 반환
	}

	double simpleCalc(double n1, double n2, String operator) { // 이항 연산자를 계산하는 함수
		double result = 0.0;

		if (operator.equals("+")) // +일 경우 n1과 n2를 더함
			result = n1 + n2;
		else if (operator.equals("-")) // -일 경우 n1과 n2를 뺌
			result = n1 - n2;
		else if (operator.equals("*")) // *일 경우 n1과 n2를 곱함
			result = n1 * n2;
		else if (operator.equals("/")) // /일 경우 n1을 n2로 나눔
			result = n1 / n2;
		else if (operator.equals("^")) // ^일 경우 n1이 밑 n2가 지수
			result = Math.pow(n1, n2);
		else if (operator.equals("log")) // log일 경우 n1이 밑 n2가 지수
			result = Math.log10(n2) / Math.log10(n1); // log10을 계산한 결과를 나눠주면 같은 결과

		return result;
	}

	void infix2Postfix(String[] infix) { // infix를 postfix로 변환해주는 함수
		int idx = 0;
		if (isOperator(infix[0])) // 수식의 첫 글자가 연산자라면
			printWrongExp(); // 에러 출력

		for (int i = 0; i < infix.length; i++) {
			if (Character.isDigit(infix[i].charAt(0))) { // 숫자인 경우, postfix 배열에 추가
				if (i != 0 && Character.isDigit(infix[i - 1].charAt(0))) {
					printWrongExp();
					return;
				}
				postfix[idx++] = infix[i];
			} else if (isOperator(infix[i])) { // 연산자인 경우,
				if (i == 0 || isOperator(infix[i - 1])) { // 연산자를 중복하여 입력한 경우
					printWrongExp(); // 에러 출력
					return;
				} else if (mySt.isStrStackEmpty()) // 연산자 스택이 비어있는 경우,
					mySt.push(infix[i]); // 스택에 push
				else {
					if (getPriority(mySt.strPeek()) < getPriority(infix[i])) {
						mySt.push(infix[i]);
						// 현재 스택의 top이 위치한 연산자와 입력받은 연산자의 우선순위를 비교
						// 입력받은 연산자의 우선순위가 더 크다면 그냥 스택에 push
					} else {
						postfix[idx++] = mySt.strPop();
						mySt.push(infix[i]);
						// 그렇지 않다면, 스택에 있는 연산자를 pop하여 postfix 배열에 넣고
						// 입력받은 연산자를 push
					}
				}
			} else if (infix[i].equals("(")) {
				mySt.push(infix[i]);
			} // 여는 괄호인 경우, 연산자 스택에 push

			else if (infix[i].equals(")")) {
				// 닫는 괄호인 경우,
				try {
					while (!mySt.strPeek().equals("(")) {
						postfix[idx++] = mySt.strPop();
					} // 여는 괄호가 나올때까지, pop하여 postfix 배열에 넣음.
					mySt.strPop(); // 스택에 있는 여는 괄호도 pop함.
				} catch (ArrayIndexOutOfBoundsException e) {
					printWrongExp();
					return;
					// 만약, 여는 괄호 개수보다 닫는 괄호가 많다면 에러 출력
				}
			} else {
				printWrongExp();
				return;
				// Operand, Operator 간의 띄어쓰기를 제대로 안하거나
				// 잘못된 입력을 했을 경우, 에러 출력
			}
		}
		while (!mySt.isStrStackEmpty()) {
			postfix[idx++] = mySt.strPop();
		} // 스택에 남아있는 연산자를 postfix 배열에 추가

		setPostfixLength(); // 변환된 후위표기식에 맞는 길이로 배열 크기 변환
		for (int i = 0; i < postfix.length; i++) {
			if (postfix[i].equals("(")) {
				printWrongExp();
				return;
			}
		}
		// 만약, 변환 완료된 후위표기식에 "("가 있다면 에러 출력
	}

	void calculatePostfix() { // postfix를 계산하는 함수
		for (int i = 0; i < postfix.length; i++) {
			if (Character.isDigit(postfix[i].charAt(0))) {
				mySt.push(Double.parseDouble(postfix[i]));
			} // 숫자라면 number stack에 push
			else if (postfix[i] == "!") {
				if (mySt.num_data[mySt.num_top] == 0)
					mySt.num_data[mySt.num_top] = 1;
				else
					mySt.num_data[mySt.num_top] = 0;
			} else if (isOperator(postfix[i])) {
				double num2 = mySt.intPop();
				double num1 = mySt.intPop();
				double temp_result = simpleCalc(num1, num2, postfix[i]);
				mySt.push(temp_result);
			} // 만약 연산자라면 number stack에서 2번 pop해서
				// 해당 연산자와 계산하고 그 결과값을 push
		}

		double res = mySt.intPeek(); // 스택에 저장된 숫자를 가져와서
		if (res == (long) res) // 정수형과 값이 동일하다면
			result = String.format("%d", (long) res); // 정수형 String으로 변환 (소수점 없애기)
		else // 실수라면
			result = String.format("%s", res); // 실수형 String으로 변환

	}

	class MyStack { // 연산자와 숫자를 스택으로 저장해줄 클래스
		int num_top, str_top; // 숫자와 연산자 스택의 인덱스
		String[] str_data; // 연산자를 저장할 String 배열
		double[] num_data; // 숫자를 저장할 double 배열

		MyStack() { 
			this.num_top = 0; // 숫자 스택 인덱스 초기화
			this.str_top = 0; // 연산자 스택 인덱스 초기화
			str_data = new String[30]; // 연산자 스택 초기화
			num_data = new double[30]; // 숫자 스택 초기화
		}

		void push(String data) { // 연산자 스택에 데이터 삽입
			this.str_data[str_top++] = data;
		}

		void push(double data) { // 숫자 스택에 데이터 삽입
			this.num_data[num_top++] = data;
		}

		String strPop() { // 연산자 스택에 가장 맨 위에 있는 데이터 반환 및 삭제
			if (str_top == 0) // 스택이 비어있는 경우
				throw new EmptyStackException(); // 예외처리
			String answer = str_data[--str_top];
			return answer; // 스택에 있는 데이터 반환
		}

		double intPop() { // 숫자 스택에 가장 맨 위에 있는 데이터 반환 및 삭제
			if (num_top == 0) // 스택이 비어있는 경우
				throw new EmptyStackException(); // 예외처리
			double answer = num_data[--num_top];
			return answer; // 스택에 있는 데이터 반환
		}

		String strPeek() { // 연산자 스택에 가장 맨 위에 있는 데이터 반환
			return this.str_data[str_top - 1];
		}

		double intPeek() { // 숫자 스택에 가장 맨 위에 있는 데이터 반환
			return this.num_data[num_top - 1];
		}

		boolean isStrStackEmpty() { // 연산자 스택이 비었는지 확인
			if (str_top == 0)
				return true;
			else
				return false;
		}

		boolean isIntStackEmpty() { // 숫자 스택이 비었는지 확인
			if (num_top == 0)
				return true;
			else
				return false;
		}

	}
}

public class Calculator extends JFrame {
	JPanel jp1; // JLabel을 포함할 panel
	JLabel jl; // 수식 및 연산 결과를 출력하는 JLabel
	JPanel jp2; // 버튼을 포함할 panel
	GridLayout layout; // 버튼을 균일하게 나눠줄 gridlayout
	JButton bt[] = new JButton[24]; // 입력 버튼 24개
	String tmp = ""; // 숫자를 저장해 줄 String 필드
	String infix = ""; // 입력된 수식들을 저장해줄 String 필드
	String[] calc; // infix를 나눠 Calculate 클래스에 적용할 String 배열

	Calculator() { // Calculator 생성자
		setTitle("21812096 최재혁 계산기"); // 생성되는 frame의 제목 

		setSize(450, 550); // GUI 창의 사이즈
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // frame을 닫았을때 프로세스도 종료시켜주는 함수

		jp1 = new JPanel(); // JLabel을 포함할 panel 초기화
		jl = new JLabel("Calculator On", SwingConstants.RIGHT); // 수식 및 연산 결과를 출력하는 JLabel 초기화
		jl.setPreferredSize(new Dimension(400, 200)); // 수식 및 연산 결과를 출력하는 JLabel의 사이즈 지정
		jl.setFont(new Font(null, Font.PLAIN, 60)); // 수식 및 연산 결과를 출력하는 JLabel의 폰트 타입 및 크기 설정
		jl.setForeground(Color.WHITE); // 출력되는 폰트 색 흰색으로 설정
		jp1.add(jl); // panel에 label 추가
		jp1.setBackground(Color.BLACK); // panel 색 검정색으로 설정

		jp2 = new JPanel(); // 버튼을 포함할 panel 초기화
		layout = new GridLayout(6, 4); // 버튼을 균일하게 나눠줄 gridlayout 초기화 및 개수 설정
		jp2.setLayout(layout); // panel에 layout 추가

		String str[] = { "(", ")", "^", "%", "C", "±", "log", "÷", "7", "8", "9", "x", "4", "5", "6", "-", "1", "2", "3", "+", "←", "0", ".", "=" }; // 버튼에 들어갈 수식 및 숫자를 저장하는 배열

		for (int i = 0; i < str.length; i++) { // 버튼에 들어갈 수식 및 숫자를 저장하는 배열 만큼 반복
			bt[i] = new JButton(String.valueOf(str[i])); // 버튼에 수식 및 숫자 삽입 및 초기화
			bt[i].setForeground(Color.WHITE); // 버튼 글자 흰색으로 설정
			if ((i >= 0 && i <= 2) || (i >= 4 && i <= 6)) { // 버튼이 (,),^,C,±,log인 경우
				bt[i].setBackground(new Color(166, 166, 166)); // 버튼 색깔 166,166,166(회색)으로 설정
				bt[i].setForeground(Color.BLACK); // 버튼 글자 검정색으로 설정
			} else if (i == 3 || i == 7 || i == 11 || i == 15 || i == 19 || i == 23) // 버튼이 %,÷,x,-,+,=인 경우
				bt[i].setBackground(new Color(255, 159, 24)); // 버튼 색깔 255,159,24(주황색)으로 설정
			else
				bt[i].setBackground(new Color(51, 51, 51)); // 나머지 버튼 색깔 51,51,51(검정색)으로 설정
			bt[i].setFont(new Font(null, Font.PLAIN, 30)); // 버튼 폰트 타입 및 크기 설정
			bt[i].addActionListener(new MyActionListener()); // 버튼 눌렸을 때 동작 설정
			bt[i].addKeyListener(new MyKeyListener()); // 버튼에 해당하는 키보드 입력 있을 때 동작 설정
			jp2.add(bt[i]); // panel에 버튼 추가
		}

		add(jp1); // frame에 수식 및 연산 결과를 출력하는 label을 포함한 panel 추가
		add(jp2, BorderLayout.SOUTH); // frame에 버튼을 포함한 panel을 아래쪽에 추가
		pack(); // 사이즈에 알맞게 조절

		Dimension frameSize = getSize();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2); // 화면의 중앙에 frame 출력

		setVisible(true); // frame 보이게 설정
	}

	class MyActionListener implements ActionListener { // 버튼이 눌렸을 때 작동하는 ActionListener
		public void actionPerformed(ActionEvent e) { // 이벤트가 발생했을 때
			switch (e.getActionCommand()) {
			case "C": // 버튼이 C라면
				tmp = ""; // tmp에 저장된 숫자 삭제 및 초기화
				infix = ""; // infix에 저장된 수식과 숫자 삭제 및 초기화
				jl.setText(""); // label에 출력
				break;
			case "±": // 버튼이 ±라면
				if (tmp.charAt(0) == '-') // 앞에 -가 있다면
					tmp = tmp.replace("-", ""); // - 삭제
				else // 앞에 아무것도 없다면 (+)
					tmp = "-" + tmp; // - 추가
				jl.setText(tmp); // label에 출력
				break;
			case "%": // 버튼이 %라면
				infix += tmp + " % "; // tmp에 저장된 숫자와 함께 infix에 저장
				tmp = ""; // tmp 삭제 및 초기화
				jl.setText("%"); // label에 출력
				break;
			case "÷": // 버튼이 ÷라면
				infix += tmp + " / "; // tmp에 저장된 숫자와 함께 infix에 저장
				tmp = ""; // tmp 삭제 및 초기화
				jl.setText("÷"); // label에 출력
				break;
			case "7": // 버튼이 7이라면
				tmp += "7"; // tmp에 숫자 저장
				jl.setText(tmp); // label에 출력
				break;
			case "8": // 버튼이 8이라면
				tmp += "8"; // tmp에 숫자 저장
				jl.setText(tmp); // label에 출력
				break;
			case "9": // 버튼이 9라면
				tmp += "9"; // tmp에 숫자 저장
				jl.setText(tmp); // label에 출력
				break;
			case "x": // 버튼이 x라면
				infix += tmp + " * "; // tmp에 저장된 숫자와 함께 infix에 저장
				tmp = ""; // tmp 삭제 및 초기화
				jl.setText("x"); // label에 출력
				break;
			case "4": // 버튼이 4라면
				tmp += "4"; // tmp에 숫자 저장
				jl.setText(tmp); // label에 출력
				break;
			case "5": // 버튼이 5라면
				tmp += "5"; // tmp에 숫자 저장
				jl.setText(tmp); // label에 출력
				break;
			case "6": // 버튼이 6이라면
				tmp += "6"; // tmp에 숫자 저장
				jl.setText(tmp); // label에 출력
				break;
			case "-": // 버튼이 -라면
				infix += tmp + " - "; // tmp에 저장된 숫자와 함께 infix에 저장
				tmp = ""; // tmp 삭제 및 초기화
				jl.setText("-"); // label에 출력
				break;
			case "1": // 버튼이 1이라면
				tmp += "1"; // tmp에 숫자 저장
				jl.setText(tmp); // label에 출력
				break;
			case "2": // 버튼이 2라면
				tmp += "2"; // tmp에 숫자 저장
				jl.setText(tmp); // label에 출력
				break;
			case "3": // 버튼이 3이라면 
				tmp += "3"; // tmp에 숫자 저장
				jl.setText(tmp); // label에 출력
				break;
			case "+": // 버튼이 +라면
				infix += tmp + " + "; // tmp에 저장된 숫자와 함께 infix에 저장
				tmp = ""; // tmp 삭제 및 초기화
				jl.setText("+"); // label에 출력
				break;
			case "←": // 버튼이 ←라면
				if (!tmp.equals("")) // tmp가 비어있지 않다면
					tmp = tmp.substring(0, tmp.length() - 1); // tmp의 마지막에 있는 숫자 하나 삭제
				jl.setText(tmp); // label에 출력
				break;
			case "0": // 버튼이 0이라면
				tmp += "0"; // tmp에 숫자 저장
				jl.setText(tmp); // label에 출력
				break;
			case ".": // 버튼이 .이라면
				tmp += "."; // tmp에 . 저장
				jl.setText(tmp); // label에 출력
				break;
			case "^": // 버튼이 ^라면
				infix += tmp + " ^ "; // tmp에 저장된 숫자와 함께 infix에 저장
				tmp = ""; // tmp 삭제 및 초기화
				jl.setText("^"); // label에 출력
				break;
			case "(": // 버튼이 (이라면
				infix += "( "; // infix에 저장
				tmp = ""; // tmp 삭제 및 초기화
				jl.setText("("); // label에 출력
				break;
			case ")": // 버튼이 )이라면
				infix += " )"; // infix에 저장
				tmp = ""; // tmp 삭제 및 초기화
				jl.setText(")"); // label에 출력
				break;
			case "log": // 버튼이 log라면
				infix += tmp + " log "; // tmp에 저장된 숫자와 함께 infix에 저장
				tmp = ""; // tmp 삭제 및 초기화
				jl.setText("log"); // label에 출력
			case "=": // 버튼이 =라면
				infix += tmp; // tmp에 저장된 숫자 infix에 저장
				calc = infix.split(" "); // infix 배열을 " " 공백이 있을 때 마다 calc 배열에 따로 저장
				jl.setText(new Calculate(calc.length, calc).result); // label에 계산결과 출력
				infix = ""; // infix 삭제 및 초기화
				tmp = ""; // tmp 삭제 및 초기화
			default:
				break;
			}
			if (tmp.length() > 16) { // 숫자의 길이가 16글자 초과이면
				tmp = tmp.substring(0, tmp.length() - 1); // 더이상 출력불가
				jl.setText(tmp); // label에 출력
			} else if (tmp.length() > 13) // 숫자의 길이가 13글자 초과이면
				jl.setFont(new Font(null, Font.BOLD, 40)); // 글자 크기 40
			else if (tmp.length() > 11) // 숫자의 길이가 11글자 초과이면
				jl.setFont(new Font(null, Font.BOLD, 50)); // 글자 크기 50
			else if (tmp.length() <= 11) // 숫자의 길이가 11글자 이하이면
				jl.setFont(new Font(null, Font.BOLD, 60)); // 글자 크기 60
		}
	}

	class MyKeyListener implements KeyListener { // 키보드가 입력됐을 때 발생하는 KeyListener
		public void keyTyped(KeyEvent e) {

		}

		public void keyPressed(KeyEvent e) { // 키가 눌렸을 때 
			switch (e.getKeyChar()) {
			case 'C': // C가 입력됐다면
				tmp = ""; // tmp에 저장된 숫자 삭제 및 초기화
				infix = ""; // infix에 저장된 수식과 숫자 삭제 및 초기화
				jl.setText(""); // label에 출력
				break;
			case 'c': // c가 입력됐다면
				tmp = ""; // tmp에 저장된 숫자 삭제 및 초기화
				infix = ""; // infix에 저장된 수식과 숫자 삭제 및 초기화
				jl.setText(""); // label에 출력
				break;
			case '%': // %가 입력됐다면
				infix += tmp + " % "; // tmp에 저장된 숫자와 함께 infix에 저장
				tmp = ""; // tmp 삭제 및 초기화
				jl.setText("%"); // label에 출력
				break;
			case '/': // /가 입력됐다면
				infix += tmp + " / "; // tmp에 저장된 숫자와 함께 infix에 저장
				tmp = ""; // tmp 삭제 및 초기화
				jl.setText("÷"); // label에 출력
				break;
			case '7': // 7이 입력됐다면
				tmp += "7"; // tmp에 숫자 저장
				jl.setText(tmp); // label에 출력
				break;
			case '8': // 8이 입력됐다면
				tmp += "8"; // tmp에 숫자 저장
				jl.setText(tmp); // label에 출력
				break;
			case '9': // 9가 입력됐다면
				tmp += "9"; // tmp에 숫자 저장
				jl.setText(tmp); // label에 출력
				break;
			case '*': // *이 입력됐다면
				infix += tmp + " * "; // tmp에 저장된 숫자와 함께 infix에 저장
				tmp = ""; // tmp 삭제 및 초기화
				jl.setText("x"); // label에 출력
				break;
			case '4': // 4가 입력됐다면
				tmp += "4"; // tmp에 숫자 저장
				jl.setText(tmp); // label에 출력
				break;
			case '5': // 5가 입력됐다면
				tmp += "5"; // tmp에 숫자 저장
				jl.setText(tmp); // label에 출력
				break;
			case '6': // 6이 입력됐다면
				tmp += "6"; // tmp에 숫자 저장
				jl.setText(tmp); // label에 출력
				break;
			case '-': // -이 입력됐다면
				infix += tmp + " - "; // tmp에 저장된 숫자와 함께 infix에 저장
				tmp = ""; // tmp 삭제 및 초기화
				jl.setText("-"); // label에 출력
				break;
			case '1': // 1이 입력됐다면
				tmp += "1"; // tmp에 숫자 저장
				jl.setText(tmp); // label에 출력
				break;
			case '2': // 2가 입력됐다면
				tmp += "2"; // tmp에 숫자 저장
				jl.setText(tmp); // label에 출력
				break;
			case '3': // 3이 입력됐다면
				tmp += "3"; // tmp에 숫자 저장
				jl.setText(tmp); // label에 출력
				break;
			case '+': // +가 입력됐다면
				infix += tmp + " + "; // tmp에 저장된 숫자와 함께 infix에 저장
				tmp = ""; // tmp 삭제 및 초기화
				jl.setText("+"); // label에 출력
				break;
			case '\b': // backspace가 입력됐다면
				if (!tmp.equals("")) // tmp가 비어있지 않다면
					tmp = tmp.substring(0, tmp.length() - 1); // tmp의 마지막에 있는 숫자 하나 삭제
				jl.setText(tmp); // label에 출력
				break;
			case '0': // 0이 입력됐다면
				tmp += "0"; // tmp에 숫자 저장
				jl.setText(tmp); // label에 출력
				break;
			case '.': // .이 입력됐다면
				tmp += "."; // tmp에 .저장
				jl.setText(tmp); // label에 출력
				break;
			case '^': // ^이 입력됐다면
				infix += tmp + " ^ "; // tmp에 저장된 숫자와 함께 infix에 저장
				tmp = ""; // tmp 삭제 및 초기화
				jl.setText("^"); // label에 출력
				break;
			case '(': // (이 입력됐다면
				infix += tmp + "( "; // infix에 저장
				tmp = ""; // tmp 삭제 및 초기화
				jl.setText("("); // label에 출력
				break;
			case ')': // )이 입력됐다면
				infix += tmp + " )"; // infix에 저장
				tmp = ""; // tmp 삭제 및 초기화
				jl.setText(")"); // label에 출력
				break;
			case '\n': // 엔터가 입력됐다면
				infix += tmp; // tmp에 저장된 숫자 infix에 저장
				calc = infix.split(" "); // infix 배열을 " " 공백이 있을 때 마다 calc 배열에 따로 저장
				jl.setText(new Calculate(calc.length, calc).result); // label에 계산결과 출력
				infix = ""; // infix 삭제 및 초기화
				tmp = ""; // tmp 삭제 및 초기화
				break;
			default:
				break;
			}
			if (tmp.length() > 16) { // 숫자의 길이가 16글자 초과이면
				tmp = tmp.substring(0, tmp.length() - 1); // 더이상 출력불가
				jl.setText(tmp); // label에 출력
			} else if (tmp.length() > 13) // 숫자의 길이가 13글자 초과이면
				jl.setFont(new Font(null, Font.BOLD, 40)); // 글자 크기 40
			else if (tmp.length() > 11) // 숫자의 길이가 11글자 초과이면
				jl.setFont(new Font(null, Font.BOLD, 50)); // 글자 크기 50
			else if (tmp.length() <= 11) // 숫자의 길이가 11글자 이하이면
				jl.setFont(new Font(null, Font.BOLD, 60)); // 글자 크기 60
		}

		public void keyReleased(KeyEvent e) {

		}

	}

	public static void main(String[] args) {
		new Calculator();
	}

}