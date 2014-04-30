DROP TABLE list;
DROP TABLE loop;

CREATE TABLE loop (
	id TEXT,
	content XML,
	createdAt TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
	updatedAt TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
	createdBy TEXT, 
	updatedBy TEXT, 
	CONSTRAINT pkLoop PRIMARY KEY (id)
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

DELETE from loop;
INSERT INTO loop(id, content, createdBy) values('@pd-1', '<loop><body>Summer Holiday<tags-box><tag type="usertag">@po</tag></tags-box></body></loop>', 'pd');
INSERT INTO loop(id, content, createdBy) values('@pd-2', '<loop><body>Book hotel<tag type="usertag">@pd-1</tag><tags-box></tags-box></body></loop>', 'pd');


SELECT id, content ::text, createdAt, createdBy FROM loop WHERE id = 'pd-1';




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