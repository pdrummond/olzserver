CREATE EXTENSION "uuid-ossp";

DROP TABLE list;
DROP TABLE loop;
DROP TABLE pod;

CREATE TABLE pod (
	id BIGSERIAL, 
	name TEXT NOT NULL,
	nextNumber BIGSERIAL, 
	createdAt TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
	updatedAt TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
	createdBy TEXT, 
	updatedBy TEXT,
	CONSTRAINT podPk PRIMARY KEY (id)
);

insert into loop (id, content) values ('51e8c936-b8b3-47d6-82c8-5917ff65252d', '@pd: This is my loop');
CREATE TABLE loop (
	id TEXT,
	content TEXT,
	podId BIGSERIAL NOT NULL, 
	showInnerLoops BOOLEAN DEFAULT FALSE,
	filterText TEXT, 
	createdAt TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
	updatedAt TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
	createdBy TEXT, 
	updatedBy TEXT, 
	CONSTRAINT loopPk PRIMARY KEY (id)	
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

CREATE TABLE users (
      userId TEXT NOT NULL,
      password TEXT NOT NULL,
      email TEXT NOT NULL, 
      enabled boolean NOT NULL,
	  createdAt TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
      CONSTRAINT pkUsers PRIMARY KEY (userId)
);

create table authorities (
      userId TEXT NOT NULL,
      authority TEXT NOT NULL,
      CONSTRAINT fkAuthoritiesUsers FOREIGN KEY (userId) REFERENCES users(userId)  
);

DROP TABLE authorities;
DROP TABLE users;

DELETE FROM loop;
DELETE FROM pod;

SELECT username, authority FROM authorities WHERE username = 'pd';

insert into users (userId, password, email, enabled) values('pd', 'pd', 'paul.drummond@iode.co.uk', true);
insert into users (userId, password, email, enabled) values('po', 'po', 'swati.kashyap@iode.co.uk', true);
insert into authorities values ('pd', 'ROLE_ADMIN');
insert into authorities values ('po', 'ROLE_USER');

insert into list (id, loopId, name, query) values ('list2', '51e8c936-b8b3-47d6-82c8-5917ff65252d', 'Comments', '#comment');

select * from loop where id = '@89b249f3-c650-4c70-99be-e59d34c4b5bc'

select * from loop where podId = 16 and id <> '@iode';
SELECT id, content, filterText, showInnerLoops, createdAt, createdBy FROM loop WHERE id ~ '(#[^@/][\\w-]*)|(@[^#/][\\w-]*)' ORDER BY updatedAt DESC

select * from pod;

select * from list where loopId = '51e8c936-b8b3-47d6-82c8-5917ff65252d';


select id, content from loop;
select id, podId, content ::text from loop;

SELECT id, content ::text, createdAt, createdBy FROM loop;

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