
var ProductRow = React.createClass({
    render: function() {
	return (<div className="row"><div className="col-xs-12"><h3>{this.props.product.name}</h3></div></div>);
    }
});

var ProductTagsRow = React.createClass({
    render: function() {
	return (<div className="row lead"><div className="col-xs-12"><i>{this.props.tags}</i></div></div>);
    }
});

var ProductPricePointRow = React.createClass({
    render: function() {
	return(
	<div className="row">
	<div className="col-xs-2 col-xs-offset-4">{this.props.currency}</div>
	<div className="col-xs-2"><span className="badge">{this.props.amount/100}</span></div>
	</div>);
    }
});

var ProductList = React.createClass({
    render: function() {
	var rows = [];
	this.props.products.forEach(function(product) {
	    rows.push(<ProductRow product={product} key={product.productId} />);
	    var listedTags = '';
	    product.tags.forEach(function(tag) {
		listedTags = listedTags + " " + tag
	    });
	    rows.push(<ProductTagsRow tags={listedTags} key={product.productId + listedTags}/>);

	    for (var key in product.pricePoints) {
          if (product.pricePoints.hasOwnProperty(key)) {
            rows.push(<ProductPricePointRow
            			  currency={key} amount={product.pricePoints[key]} key={product.productId + key} />
            		 );

          }
        }

	});
	return(
		<div>{rows}</div>
	);
    }
});

class App extends React.Component {

	constructor(props) {
		super(props);
		this.state = {products: []};
		 this.handleResponse = this.handleResponse.bind(this);
		 this.handleJson = this.handleJson.bind(this);
	}

	componentDidMount() {
        fetch(new Request("http://localhost:8080/api/products")).then(this.handleResponse);
	}

	render() {
		return (
		    <div>
		    <h1>Envision</h1>
		    <div className="container">
		    <div className="row row-centered">
		    <div className="col-xs-6 col-xs-offset-3">
		    <ProductList products={this.state.products}/>
		    </div>
		    </div>
		    </div>
		    </div>
		)
	}

	handleResponse(response) {
        response.json().then(this.handleJson);
	}

	 handleJson(json){
	     this.setState({
             products: json._embedded.products
         });
	}
}

ReactDOM.render(
	<App />,
	document.getElementById('content')
)