CREATE EXTENSION "uuid-ossp";

DELETE FROM list;
DELETE FROM loop;
insert into loop (id, content) values ('#outerloop@openloopz', '<loop><loop-header><b>Welcome to The Outer Loop</b></loop-header><loop-body></loop-body><loop-footer><tag type="hashtag">#public@openloopz</tag></loop-footer></loop>');
insert into list (id, loopId, name, query) values ('outerloop-list', '#outerloop@openloopz', 'Loops', ''); 


DROP TABLE authorities;
DROP TABLE users;

DROP TABLE list;
DROP TABLE loop;
DROP TABLE pod;

CREATE TABLE pod (
	id BIGSERIAL, 
	name TEXT NOT NULL,
	config TEXT NOT NULL DEFAULT '',
	podType BIGINT NOT NULL DEFAULT 1,
	createdAt TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
	updatedAt TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
	createdBy TEXT, 
	updatedBy TEXT,
	CONSTRAINT podPk PRIMARY KEY (id)
);

INSERT INTO pod(id, name) values(1, 'The OuterLoop');
INSERT INTO pod(id, name) values(2, 'Paul Drummond');
INSERT INTO pod(id, name) values(3, 'Em');

CREATE TABLE loop (
	id TEXT,
	ownerTag TEXT,
	content XML,
	podId BIGINT NOT NULL DEFAULT 1, 
	createdAt TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
	updatedAt TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
	createdBy TEXT, 
	updatedBy TEXT, 
	CONSTRAINT loopPk PRIMARY KEY (id),
	CONSTRAINT fk1Pod FOREIGN KEY (podId) REFERENCES pod (id)
);

CREATE TABLE list (
	id TEXT, 
	loopId TEXT,
	name TEXT NOT NULL,
	query TEXT NOT NULL,	
	comparator TEXT NOT NULL DEFAULT 'updatedAt',
	sortOrder TEXT NOT NULL DEFAULT 'ascending',
	createdAt TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
	updatedAt TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
	createdBy TEXT, 
	updatedBy TEXT, 
	CONSTRAINT pkList PRIMARY KEY (id)	
);

CREATE TABLE users (
      userId TEXT NOT NULL,
      nextLoopId BIGINT NOT NULL DEFAULT 1, 
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

insert into users (userId, password, email, enabled) values('pd', 'pd', 'paul.drummond@iode.co.uk', true);
insert into users (userId, password, email, enabled) values('po', 'po', 'swati.kashyap@iode.co.uk', true);
insert into users (userId, password, email, enabled) values('openloopz', 'openloopz', 'openloopz@iode.co.uk', true);
insert into users (userId, password, email, enabled) values('iode', 'iode', 'enquiries@iode.co.uk', true);
insert into authorities values ('pd', 'ROLE_ADMIN');
insert into authorities values ('po', 'ROLE_USER');
insert into authorities values ('openloopz', 'ROLE_ADMIN');
insert into authorities values ('iode', 'ROLE_ADMIN');

delete from authorities;
delete from users;

select * from loop where podId in (1, 2);


SELECT * from list;

select * from loop where TIMESTAMP(updatedAt) > 1401117232933;

SELECT username, authority FROM authorities WHERE username = 'pd';


update loop set content = '@po: This is my first loop Pauloo! @pd' where id = '#fc4b600d-2c96-4c25-954a-0044af9f897b';

update loop set content = '@pd: Hello @po!' where id = '#d2e23a54-1078-4f01-89c8-b10cdd538b73';




insert into list (id, loopId, name, query) values ('list2', '#a14c5b9e-2aab-497a-8b4c-648ee6782c90', 'Comments', '#comment');

select * from loop where updatedAt > '2014-05-26 17:52:33.527';									  



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

SELECT * FROM loops WHERE content LIKE '%#journal%';

update users set nextLoopId = 8 where userId = 'pd'