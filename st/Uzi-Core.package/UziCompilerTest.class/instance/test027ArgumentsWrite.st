tests
test027ArgumentsWrite
	| program script |
	program := self compile: 'proc mul(a, b) { b = a * b; }'.
	script := program scripts first.
	self assert: #(a b) equals: (script arguments collect: [:each | each name]).
	self assert: script instructions = {
		readLocal arg: (script arguments detect: [:var | var name = #a]).
		readLocal arg: (script arguments detect: [:var | var name = #b]).
		prim arg: (UziPrimitive named: #multiply).
		writeLocal arg: (script arguments detect: [:var | var name = #b])
	}