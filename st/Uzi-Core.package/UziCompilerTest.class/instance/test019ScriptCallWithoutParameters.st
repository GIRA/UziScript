tests
test019ScriptCallWithoutParameters
	| program script |
	program := self compile: '
	var a;
	task main() running { a = foo(); }
	func foo() { return 42; }'.
	script := program scripts first.
	self assert: script instructions = {
		call arg: #foo.
		pop arg: (program globals detect: [:var | var name = 'a']).
	}