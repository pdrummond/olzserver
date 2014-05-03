DROP TABLE list;
DROP TABLE loop;
DROP TABLE slice;

CREATE TABLE slice (
	id BIGSERIAL, 
	name TEXT NOT NULL,
	nextNumber BIGSERIAL, 
	createdAt TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
	updatedAt TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
	createdBy TEXT, 
	updatedBy TEXT,
	CONSTRAINT slicePk PRIMARY KEY (id)
);

CREATE TABLE loop (
	id TEXT,
	sliceId BIGSERIAL NOT NULL, 
	content XML,
	showInnerLoops BOOLEAN DEFAULT FALSE,
	createdAt TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
	updatedAt TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
	createdBy TEXT, 
	updatedBy TEXT, 
	CONSTRAINT loopPk PRIMARY KEY (id),
	CONSTRAINT loopSliceFk FOREIGN KEY (sliceId) REFERENCES slice (id)	
);

CREATE TABLE list (
	id TEXT, 
	loopId TEXT,
	name TEXT NOT NULL,
	query TEXT NOT NULL,	
	createdAt TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
	updatedAt TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
	createdBy TEXT, 
	updatedBy TEXT, 
	CONSTRAINT pkList PRIMARY KEY (id),
	CONSTRAINT fk1List FOREIGN KEY (loopId) REFERENCES loop (id)	
);


DELETE FROM slice;
DELETE FROM loop;

INSERT INTO slice(id, name, nextNumber) values(1, 'iode', 3);
INSERT INTO loop(id, sliceId, content, createdBy) values('@pd', 1, '<loop><body><b>Paul Drummond</b></body></loop>', 'pd');
INSERT INTO loop(id, sliceId, content, createdBy) values('@1', 1, '<loop><body>Summer Holiday<loop-ref>@pd</loop-ref></body></loop>', 'pd');
INSERT INTO loop(id, sliceId, content, createdBy) values('@2', 1, '<loop><body>Book hotel for <loop-ref>@holiday</loop-ref><loop-ref>@pd></loop-ref></body></loop>', 'pd');

select * from loop;

select id, sliceId, content ::text from loop;

SELECT id, content ::text, createdAt, createdBy FROM loop WHERE id = 'pd-1';

SELECT id, (xpath('//loop-ref/text()', content))::text as loop_refs FROM loop;




SELECT id, (xpath('//tag[@type="usertag"]/text()', content)) from loops;
SELECT id, (xpath('//tag[@type="usertag"]/text()', content)) from loops;
SELECT id from loops where (xpath('//tag[@type="usertag"]/text()', content));   

SELECT id from loops where xpath('//tag[text() = "pd"] | //tag[text() = "po"]', content) @> ('pd', 'po');

SELECT id, content ::text from loops where (xpath('//tag[text() = "pd"] | //tag[text() = "po"]', content)); == 2;

SELECT id, content ::text from loops where (xpath('//tag[@type="usertag"]/text()', content))[1]::text in ('pd', 'po');  


SELECT id, content from loops l, refs r where r.ref_tag_id = l.id and l.id = 'journal';

SELECT id, content ::text, created_at, created_by from loops l, refs r WHERE r.ref_id = l.id AND r.ref_tag_id = 'journal';

select * from refs;

select id from loops;

SELECT uid, lid, content ::text from loops;

SELECT * FROM loops WHERE content LIKE '%boom%'

UPDATE loops SET content = 'This is loop11' WHERE lid = 'loop11'

SELECT * FROM loops WHERE content LIKE '%#journal%'