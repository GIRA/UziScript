private
resolveImport: import
	(librariesDirectory fileExists: import path) ifFalse: [^ nil].
	^ librariesDirectory 
		readOnlyFileNamed: import path
		do: [:file || ast |
			ast := parser parse: file contents.
			ast isPetitFailure 
				ifTrue: [ast]
				ifFalse: [UziAliasApplier apply: import alias , '.' on: ast]]