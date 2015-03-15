



/**
 * Neural Network Prototype
 * @param {type} activator
 * @returns {NeuralNetwork}
 */
function NeuralNetwork(activator){
    
    this.activator = activator;
    this.neurons = [];
    this.bias = new Neuron(activator);
}


/**
 * Generate the structure of a dynamicly assigned network
 * @returns {undefined}
 */
NeuralNetwork.prototype.generateStructure = function(){
    
    this.neurons = [];
    var list = this.list();
    
    // locate inputs and reset neuron layer designator
    var inputs = [];
    
    for(var i = 0; i < list.length; i++){
        
        var n = list[i];
        n.layer = -1;
        
        if(n.inputs.length === 0){
            inputs.push(n);
            n.layer = 0;
            list.splice(i,1);
        }
    }
    
    this.neurons.push(inputs);
    
    
    // locate the rest of the neurons
    var counter = 0;
    var max = list.length*list.length;
    
    while(list.length > 0 && counter < max){
       
        for(var i = 0; i < list.length; i++){
           
            var n = list[i];
            var synapses = n.inputs;
            var len = synapses.length;

            for(var j = 0; j < len; j++){

                var parent = synapses[j].input;

                if(parent.layer !== -1){
                    n.layer = parent.layer + 1;
                    list.splice[i,1];
                    
                    if(this.neurons.length <= n.layer){
                        this.neurons.push([]);
                    }
                    
                    this.neurons[n.layer].push(n);
                    break;
                }
            }
        }
    }
    
    counter++;
};


/**
 * Get all nodes in the network
 * @returns {Array}
 */
NeuralNetwork.prototype.list = function(){
  
    var synapses = this.bias.outputs;
    var len = synapses.length;
    var arr = [];
    
    for(var i = 0; i < len; i++){
        arr.push[synapses[i].output];
    }
    
    return arr;
};


/**
 * Run the network once with the given input
 * @param {type} input
 * @returns {undefined}
 */
NeuralNetwork.prototype.run = function(input){
    
    var layer = this.neurons[0];
    var len = Math.min(layer.length, input.length);
    var nlen = this.neurons.length;
    
    for(var i = 0; i < len; i++){
        layer.value = input[i];
    }
    
    for(var l = 0; l < nlen; l++){
        
        var layer = this.neurons[l];
        len = layer.length;
        
        for(var i = 0; i < len; i++){
            layer[i].fire();
        }
    }
};


/**
 * Get result output
 * @returns {Array|NeuralNetwork.prototype.result.arr}
 */
NeuralNetwork.prototype.result = function(){
  
    var arr = [];
    var output = this.neurons[this.neurons.length - 1];
    var len = output.length;
    
    for(var i = 0; i < len; i++){
        arr.push(output[i].value);
    }
    
    return arr;
};


/**
 * Run the backpropagation algorithm
 * @param {type} input
 * @param {type} desired
 * @param {type} learningRate
 * @returns {undefined}
 */
NeuralNetwork.prototype.backpropagation = function(input, desired, learningRate){
  
    // run network
    this.run(input);
    
    // calculate error for output layer
    var outputLayer = this.neurons[this.neurons.length - 1];
    var len = Math.min(outputLayer.length, desired.length);
    
    for(var i = 0; i < len; i++){
        outputLayer[i].calculateError(desired[i]);
    }
    
    // calculate error for hidden layer
    for(var l = this.neurons.length - 2; l >= 0; l--){
        
        var hiddenLayer = this.neurons[l];
        len = hiddenLayer.length;
        
        for(var i = 0; i < len; i++){
            hiddenLayer[i].calculateRecurrent();
        }
    }
    
    // adjust bias weights
    this.bias.adjustWeights(learningRate);
   
    // adjust network weights
    var nlen = this.neurons.length;
    
    for(var l = 0; l < nlen; l++){
        
        var layer = this.neurons[l];
        len = layer.length;
        
        for(var i = 0; i < len; i++){
            
            var n = layer[i];
            n.adjustWeights(learningRate);
            n.value = n.value + learningRate*n.error;
        }
    }
};


function Neuron(network){
    // Todo bias and bias synapse, does not need render etc
    this.activator = network.activator;
    this.inputs = [];
    this.outputs = [];
    this.value = 0;
    this.error = 0;
    this.layer = 0;
    
    if(typeof network.bias !== "undefined"){
        network.bias.connect(this);
    }
}


Neuron.prototype.fire = function(){
    
    var len = this.inputs.length;
    var transfer = 0.0;
    
    for(var i = 0; i < len; i++){
        var input = this.inputs[i];
        transfer = transfer + input.input.value*input.weight;
    }
    
    this.value = this.activator(transfer);
};


Neuron.prototype.calculateError = function(desired){
    var val = this.value;
    this.error = val*(1.0 - val) * (desired - val);
};


Neuron.prototype.calculateRecurrent = function(){
  
    var recurrent = 1.0;
    var len = this.outputs.length;
    
    for(var i = 0; i < len; i++){
        var s = this.outputs[i];
        recurrent = recurrent*s.weight*s.output.error;
    }
    
    var val = this.value;
    this.error = val*(1.0-val)*recurrent;
};


Neuron.prototype.adjustWeights = function(learningRate){
    
    var len = this.outputs.length;
    var val = this.value; // TODO test with children value
    
    for(var i = 0; i < len; i++){
        var s = this.outputs[i];
        s.weight = s.weight + learningRate*s.output.error*val;
    }
    
};


/**
 * Connect two neurons
 * @param {type} neuron
 * @returns {undefined}
 */
Neuron.prototype.connect = function(neuron){
    
    var synapse = new Synapse();
    
    synapse.input = this;
    synapse.output = neuron;
    
    this.outputs.push(synapse);
    neuron.inputs.push(synapse);
};



/**
 * Disconnect two neurons, run this on both neurons to disconnect
 * @param {type} neuron
 * @returns {undefined}
 */
Neuron.prototype.disconnect = function(neuron){
  
    // check if output neuron
    var len = this.outputs.length;
    
    for(var i = 0; i < len; i++){
        
        var s = this.outputs[i];
        
        if(s.output === neuron){
            this.outputs.splice(i,1);
            return true;
        }
    }
    
    
    // check if input neuron
    len = this.inputs.length;
    
    for(var i = 0; i < len; i++){
        
        var s = this.inputs[i];
        
        if(s.input === neuron){
            this.inputs.splice(i,1);
            return true;
        }
    }
    
    return false;
};


function Synapse(){
    this.input = null;
    this.output = null;
    this.weight = 1;
}


function Activator(){
    
    this.sigmoid = function(sum){
        return (1.0/(1.0+Math.exp(-sum)));
    };
    
    
}