var OlzApp = {};

$(function () {

	OlzApp.LoginView = Backbone.View.extend({

		initialize: function() {
			this.template =  _.template($('#login-template')).html();
			this.render();
		},

		render: function() {
			this.$el.html(this.template(this.model));

			var loginForm = $('#loginForm').get(0);

			$(loginForm).on('submit', function(e) {
				e.preventDefault();

				$.when($.get('login')).then(
						function(data, textStatus, jqXHR) {
							var token = jqXHR.getResponseHeader('X-CSRF-TOKEN');
							var csrf = $('<input/>', {
								type: 'hidden',
								name: jqXHR.getResponseHeader('X-CSRF-PARAM'),
								value: token
							});
							$(loginForm).append(csrf);
							loginForm.submit();
						},
						function(error) {
							console.error(error);
						}
				);
			});

			return this;
		}

	});
});


