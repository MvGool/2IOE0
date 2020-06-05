package engine.graphics;

import static org.lwjgl.opengl.GL20.*;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import engine.maths.Matrix4f;
import engine.maths.Vector2f;
import engine.maths.Vector3f;

public class ShaderProgram {

    private final int programId;

    private int vertexShaderId;

    private int fragmentShaderId;
    
    private final Map<String, Integer> uniforms;

    public ShaderProgram() throws Exception {
        programId = glCreateProgram();
        if (programId == 0) {
            throw new Exception("Could not create Shader");
        }
        
        uniforms = new HashMap<>();
    }

    public void createVertexShader(String shaderCode) throws Exception {
        vertexShaderId = createShader(shaderCode, GL_VERTEX_SHADER);
    }

    public void createFragmentShader(String shaderCode) throws Exception {
        fragmentShaderId = createShader(shaderCode, GL_FRAGMENT_SHADER);
    }

    protected int createShader(String shaderCode, int shaderType) throws Exception {
        int shaderId = glCreateShader(shaderType);
        if (shaderId == 0) {
            throw new Exception("Error creating shader. Type: " + shaderType);
        }

        glShaderSource(shaderId, shaderCode);
        glCompileShader(shaderId);

        if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == 0) {
            throw new Exception("Error compiling Shader code: " + glGetShaderInfoLog(shaderId, 1024));
        }

        glAttachShader(programId, shaderId);

        return shaderId;
    }

    public void link() throws Exception {
        glLinkProgram(programId);
        if (glGetProgrami(programId, GL_LINK_STATUS) == 0) {
            throw new Exception("Error linking Shader code: " + glGetProgramInfoLog(programId, 1024));
        }

        if (vertexShaderId != 0) {
            glDetachShader(programId, vertexShaderId);
        }
        if (fragmentShaderId != 0) {
            glDetachShader(programId, fragmentShaderId);
        }

        glValidateProgram(programId);
        if (glGetProgrami(programId, GL_VALIDATE_STATUS) == 0) {
            System.err.println("Warning validating Shader code: " + glGetProgramInfoLog(programId, 1024));
        }

    }
    
    public void createUniform(String name) throws Exception {
		int uniformLocation = glGetUniformLocation(programId, name);
		if (uniformLocation < 0) {
			throw new Exception("Could not find uniform:" + name);
		}
		
		uniforms.put(name, uniformLocation);
	}
    
	public int getUniformLocation(String name) {
		return GL20.glGetUniformLocation(programId, name);
	}
	
	public void setUniform(String name, float value) {
		GL20.glUniform1f(getUniformLocation(name), value);
	}
	
	public void setUniform(String name, int value) {
		GL20.glUniform1i(getUniformLocation(name), value);
	}
	
	public void setUniform(String name, boolean value) {
		GL20.glUniform1i(getUniformLocation(name), value ? 1 : 0);
	}
	
	public void setUniform(String name, Vector2f value) {
		GL20.glUniform2f(getUniformLocation(name), value.getX(), value.getY());
	}
	
	public void setUniform(String name, Vector3f value) {
		GL20.glUniform3f(getUniformLocation(name), value.getX(), value.getY(), value.getZ());
	}
	
	public void setUniform(String name, Matrix4f value) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
	        FloatBuffer fb = stack.mallocFloat(16);
	        value.getMatrixFloatBuffer(fb);
	        glUniformMatrix4fv(uniforms.get(name), false, fb);
	    }
	}

	public void setUniform(String name, Matrix4f[] values) {
		FloatBuffer matrix = MemoryUtil.memAllocFloat(Matrix4f.SIZE * Matrix4f.SIZE * values.length);
		for (int i = 0; i < values.length; i++) {
			values[i].getMatrixFloatBuffer(16 * i, matrix);
		}
		GL20.glUniformMatrix4fv(getUniformLocation(name), true, matrix);
	}

	public void setUniform(String name, org.joml.Matrix4f[] values) {
		FloatBuffer matrix = MemoryUtil.memAllocFloat(16 * values.length);
		//matrix.put(value.getMatrix()).flip();
		for (int i = 0; i < values.length; i++) {
			values[i].get(16 * i, matrix);
		}
		GL20.glUniformMatrix4fv(getUniformLocation(name), true, matrix);
	}

    public void bind() {
        glUseProgram(programId);
    }

    public void unbind() {
        glUseProgram(0);
    }

    public void cleanup() {
        unbind();
        if (programId != 0) {
            glDeleteProgram(programId);
        }
    }
}