tests
test005ProgramWithFloatVars
	| program encoded vars |
	program := Uzi program: [:p | p
		script: #blink
		ticking: true
		delay: 0
		instructions: [:s | s
			push: 0.5;		
			write: 13]].
	encoded := UziEncoder new encode: program.
	vars := encoded copyFrom: 2 to: 9.
	self assert: #[2 4 0 7 63 0 0 0] equals: vars